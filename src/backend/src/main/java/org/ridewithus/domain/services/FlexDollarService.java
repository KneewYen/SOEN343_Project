package org.ridewithus.domain.services;

import org.ridewithus.domain.entity.FlexDollarTransaction;
import org.ridewithus.domain.entity.User;
import org.ridewithus.infrastructure.repository.FlexDollarTransactionRepository;
import org.ridewithus.infrastructure.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FlexDollarService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FlexDollarTransactionRepository transactionRepository;

    /**
     * Apply Flex Dollars to reduce a trip cost.
     * Returns the amount of Flex Dollars that were actually applied.
     * 
     * @param userId The user ID
     * @param tripCost The total cost of the trip
     * @param tripId The trip ID for reference
     * @return The amount of Flex Dollars applied (will be <= tripCost and <= user's balance)
     */
    @Transactional
    public int applyFlexDollars(Long userId, double tripCost, Long tripId) {
        if (tripCost <= 0) {
            return 0; // No cost, nothing to apply
        }
        
        // Get user's current balance
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        int currentBalance = user.getFlexDollars();
        
        // If user has no balance, nothing to apply
        if (currentBalance <= 0) {
            return 0;
        }
        
        // Calculate how much we can apply (min of balance and trip cost)
        // Use floor to ensure we don't apply partial dollars
        int amountToApply = (int) Math.min(currentBalance, Math.floor(tripCost));
        
        if (amountToApply > 0) {
            // Apply Flex Dollars using the repository method (atomic operation)
            // This SQL query ensures balance never goes below zero
            int rowsUpdated = userRepository.applyFlexDollarsById(userId, amountToApply);
            
            if (rowsUpdated > 0) {
                // Create transaction record for ledger
                FlexDollarTransaction transaction = FlexDollarTransaction.builder()
                        .userId(userId)
                        .amount(amountToApply)
                        .type(FlexDollarTransaction.TransactionType.DEBIT)
                        .reference("trip_payment:" + tripId)
                        .build();
                
                transactionRepository.save(transaction);
                
                return amountToApply;
            } else {
                // User may have been deleted or balance changed between check and apply
                // Return 0 to indicate no Flex Dollars were applied
                return 0;
            }
        }
        
        return 0;
    }
    
    /**
     * Apply a specific amount of Flex Dollars to reduce a trip cost.
     * Validates that the amount doesn't exceed balance or trip cost.
     * 
     * @param userId The user ID
     * @param amountToApply The specific amount of Flex Dollars to apply (user-selected)
     * @param tripCost The total cost of the trip
     * @param tripId The trip ID for reference
     * @return The amount of Flex Dollars actually applied
     */
    @Transactional
    public int applyFlexDollarsAmount(Long userId, int amountToApply, double tripCost, Long tripId) {
        if (amountToApply <= 0) {
            return 0; // No amount specified, nothing to apply
        }
        
        if (tripCost <= 0) {
            return 0; // No cost, nothing to apply
        }
        
        // Get user's current balance
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        int currentBalance = user.getFlexDollars();
        
        // Validate amount doesn't exceed balance
        if (amountToApply > currentBalance) {
            throw new IllegalArgumentException("Cannot apply more Flex Dollars than available balance. Available: " + currentBalance + ", Requested: " + amountToApply);
        }
        
        // Validate amount doesn't exceed trip cost
        int maxApplicable = (int) Math.floor(tripCost);
        if (amountToApply > maxApplicable) {
            amountToApply = maxApplicable; // Cap at trip cost
        }
        
        if (amountToApply > 0) {
            // Apply Flex Dollars using the repository method (atomic operation)
            int rowsUpdated = userRepository.applyFlexDollarsById(userId, amountToApply);
            
            if (rowsUpdated > 0) {
                // Create transaction record for ledger
                FlexDollarTransaction transaction = FlexDollarTransaction.builder()
                        .userId(userId)
                        .amount(amountToApply)
                        .type(FlexDollarTransaction.TransactionType.DEBIT)
                        .reference("trip_payment:" + tripId)
                        .build();
                
                transactionRepository.save(transaction);
                
                return amountToApply;
            } else {
                // User may have been deleted or balance changed between check and apply
                return 0;
            }
        }
        
        return 0;
    }

    /**
     * Get the current Flex Dollar balance for a user.
     */
    public int getBalance(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
        return user.getFlexDollars();
    }

    /**
     * Get all Flex Dollar transactions for a user.
     */
    public java.util.List<FlexDollarTransaction> getTransactions(Long userId) {
        return transactionRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    /**
     * Award (credit) Flex Dollars to a user's account.
     * This is used when a rider returns a bike to a low-occupancy station.
     * Creates a ledger entry for the transaction.
     * 
     * @param userId The user ID
     * @param amount The amount of Flex Dollars to award (typically 1)
     * @param stationId The station ID where the bike was returned
     * @param tripId The trip ID (optional, can be null)
     * @return The updated Flex Dollar balance after the award
     */
    @Transactional
    public int creditFlexDollars(Long userId, int amount, Long stationId, Long tripId) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount to credit must be greater than 0");
        }
        
        // Use atomic increment to update balance
        int rowsUpdated = userRepository.incrementFlexDollarsById(userId, amount);
        
        if (rowsUpdated <= 0) {
            throw new RuntimeException("Failed to credit Flex Dollars to user: " + userId + ". User may not exist.");
        }
        
        // Refresh user entity to get updated balance (ensures we have the latest value)
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found after credit operation: " + userId));
        
        int updatedBalance = user.getFlexDollars();
        
        // Create transaction record for ledger
        String reference = tripId != null 
                ? String.format("station_return:station=%d,trip=%d", stationId, tripId)
                : String.format("station_return:station=%d", stationId);
        
        FlexDollarTransaction transaction = FlexDollarTransaction.builder()
                .userId(userId)
                .amount(amount)
                .type(FlexDollarTransaction.TransactionType.CREDIT)
                .reference(reference)
                .build();
        
        transactionRepository.save(transaction);
        
        return updatedBalance;
    }
}

