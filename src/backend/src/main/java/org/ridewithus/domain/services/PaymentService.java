package org.ridewithus.domain.services;

import org.ridewithus.domain.entity.Billing;
import org.ridewithus.domain.entity.Trip;
import org.ridewithus.domain.entity.User;
import org.ridewithus.infrastructure.repository.BillingRepository;
import org.ridewithus.infrastructure.repository.TripRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class PaymentService {

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private PricingService pricingService;

    @Autowired
    private FlexDollarService flexDollarService;

    @Autowired
    private BillingRepository billingRepository;

    // In a real implementation, this would integrate with external payment services
    // like Stripe, PayPal, Square, etc.
    
    public Map<String, Object> processPayment(Long tripId, Double amount, String paymentMethod, Map<String, Object> paymentDetails) {
        Trip trip = tripRepository.findByTripId(tripId);
        
        if (trip == null) {
            throw new RuntimeException("Trip not found");
        }

        // Validate payment amount matches trip cost
        // In a real implementation, you would calculate the actual cost here
        // For now, we'll accept any amount

        // Process payment based on method
        String transactionId = processPaymentByMethod(paymentMethod, paymentDetails, amount);

        // Update trip payment status (you may want to add a paymentStatus field to Trip entity)
        // For now, we'll just return success

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("transactionId", transactionId);
        response.put("tripId", tripId);
        response.put("amount", amount);
        response.put("paymentMethod", paymentMethod);
        response.put("message", "Payment processed successfully");

        return response;
    }

    private String processPaymentByMethod(String paymentMethod, Map<String, Object> paymentDetails, Double amount) {
        // Generate a transaction ID
        String transactionId = UUID.randomUUID().toString();

        switch (paymentMethod.toLowerCase()) {
            case "card":
                // In a real implementation, this would:
                // 1. Validate card details
                // 2. Send to payment processor (Stripe, Square, etc.)
                // 3. Handle 3D Secure if required
                // 4. Return transaction result
                return processCardPayment(paymentDetails, amount, transactionId);
            
            case "paypal":
                // In a real implementation, this would:
                // 1. Create PayPal order
                // 2. Redirect user to PayPal
                // 3. Handle callback
                return processPayPalPayment(amount, transactionId);
            
            case "external":
                // For external payment services, you would:
                // 1. Create payment intent with external service
                // 2. Return redirect URL
                return processExternalPayment(amount, transactionId);
            
            default:
                throw new RuntimeException("Unsupported payment method: " + paymentMethod);
        }
    }

    private String processCardPayment(Map<String, Object> paymentDetails, Double amount, String transactionId) {
        // Mock card payment processing
        // In production, integrate with Stripe, Square, or similar service
        
        // If amount is 0, no payment processing needed
        if (amount == null || amount <= 0) {
            return transactionId;
        }
        
        // Validate payment details only if amount > 0
        if (paymentDetails == null || paymentDetails.isEmpty()) {
            // For mock purposes, allow empty details if amount is 0
            // In production, this would require actual payment details
            return transactionId;
        }
        
        String cardNumber = (String) paymentDetails.get("cardNumber");
        String expiryDate = (String) paymentDetails.get("expiryDate");
        String cvv = (String) paymentDetails.get("cvv");
        
        // Basic validation (only if card details are provided)
        if (cardNumber != null && cardNumber.length() < 13) {
            throw new RuntimeException("Invalid card number");
        }
        
        if (expiryDate != null && !expiryDate.matches("\\d{2}/\\d{2}")) {
            throw new RuntimeException("Invalid expiry date");
        }
        
        if (cvv != null && cvv.length() < 3) {
            throw new RuntimeException("Invalid CVV");
        }

        // Simulate payment processing delay
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // In production, this would call the actual payment processor API
        // For now, we'll simulate a successful payment
        return transactionId;
    }

    private String processPayPalPayment(Double amount, String transactionId) {
        // Mock PayPal payment processing
        // In production, integrate with PayPal SDK
        
        // Simulate payment processing
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        return transactionId;
    }

    private String processExternalPayment(Double amount, String transactionId) {
        // Mock external payment service processing
        // In production, integrate with your chosen external payment service
        
        // Simulate payment processing
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        return transactionId;
    }

    public Map<String, Object> getPaymentStatus(Long tripId) {
        Trip trip = tripRepository.findByTripId(tripId);
        
        if (trip == null) {
            throw new RuntimeException("Trip not found");
        }

        Map<String, Object> status = new HashMap<>();
        // In a real implementation, you would check the actual payment status
        // For now, we'll return a default status
        status.put("tripId", tripId);
        status.put("paymentStatus", "pending"); // or "paid", "failed", etc.
        status.put("amount", 0.0); // You would get this from the payment record
        
        return status;
    }

    /**
     * Process trip payment with automatic Flex Dollar application.
     * This method:
     * 1. Calculates the trip cost
     * 2. Automatically applies available Flex Dollars
     * 3. Processes remaining amount via payment method
     * 4. Updates billing record with Flex Dollar discount
     * 
     * @param tripId The trip ID
     * @param paymentMethod The payment method (card, paypal, external)
     * @param paymentDetails Payment details (card info, etc.)
     * @return Payment processing result with Flex Dollar application details
     */
    @Transactional
    public Map<String, Object> processTripPaymentWithFlexDollars(Long tripId, String paymentMethod, Map<String, Object> paymentDetails) {
        Trip trip = tripRepository.findByTripId(tripId);
        
        if (trip == null) {
            throw new RuntimeException("Trip not found");
        }

        User user = trip.getUser();
        if (user == null) {
            throw new RuntimeException("User not found for trip");
        }

        // Step 1: Calculate total trip cost
        org.ridewithus.domain.dto.BillingDTO billingDTO = pricingService.calculatePricingPlan(tripId);
        double totalCost = billingDTO.getTotalAmount();

        // Step 2: Get user's Flex Dollar balance and apply if available
        int flexDollarBalance = flexDollarService.getBalance(user.getId());
        int flexDollarDiscount = 0;
        
        // Check if a specific Flex Dollar amount was provided in paymentDetails
        Integer requestedFlexDollarAmount = null;
        if (paymentDetails != null && paymentDetails.containsKey("flexDollarAmount")) {
            Object flexDollarAmountObj = paymentDetails.get("flexDollarAmount");
            if (flexDollarAmountObj instanceof Number) {
                requestedFlexDollarAmount = ((Number) flexDollarAmountObj).intValue();
            }
        }
        
        if (flexDollarBalance > 0 && totalCost > 0) {
            if (requestedFlexDollarAmount != null && requestedFlexDollarAmount > 0) {
                // Apply specific amount requested by user
                flexDollarDiscount = flexDollarService.applyFlexDollarsAmount(
                    user.getId(), requestedFlexDollarAmount, totalCost, tripId);
            } else {
                // Auto-apply (original behavior - apply maximum available)
                flexDollarDiscount = flexDollarService.applyFlexDollars(user.getId(), totalCost, tripId);
            }
        }

        // Step 3: Calculate final amount after Flex Dollar discount
        double finalAmount = Math.max(0, totalCost - flexDollarDiscount);

        // Step 4: Update billing record with Flex Dollar discount
        Optional<Billing> billingOpt = billingRepository.findByTrip_TripId(tripId);
        if (billingOpt.isPresent()) {
            Billing billing = billingOpt.get();
            billing.setFlexDollarDiscount(flexDollarDiscount);
            billing.setTotalAmount((int) Math.round(totalCost));
            billing.setFinalAmount((int) Math.round(finalAmount));
            billingRepository.save(billing);
        }

        // Step 5: Process remaining amount via payment method (if any)
        String transactionId = null;
        if (finalAmount > 0) {
            transactionId = processPaymentByMethod(paymentMethod, paymentDetails, finalAmount);
        } else {
            // Payment fully covered by Flex Dollars
            transactionId = "FLEX_DOLLAR_ONLY_" + UUID.randomUUID().toString();
        }

        // Step 6: Get updated Flex Dollar balance (after application)
        // This ensures we return the accurate balance after the transaction
        int updatedBalance = flexDollarService.getBalance(user.getId());

        // Step 7: Build response
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("transactionId", transactionId);
        response.put("tripId", tripId);
        response.put("totalCharges", totalCost);
        response.put("flexDollarDiscount", flexDollarDiscount);
        response.put("finalAmount", finalAmount);
        response.put("flexDollarBalance", updatedBalance);
        response.put("paymentMethod", paymentMethod);
        
        // Build confirmation message
        String paymentSummary;
        if (flexDollarDiscount > 0 && finalAmount > 0) {
            paymentSummary = String.format(
                "Total: $%.2f. Applied %d Flex Dollars as discount. Charged $%.2f to your %s. Remaining Flex Dollar balance: %d.",
                totalCost, flexDollarDiscount, finalAmount, paymentMethod, updatedBalance
            );
        } else if (flexDollarDiscount > 0 && finalAmount == 0) {
            paymentSummary = String.format(
                "Total: $%.2f. Applied %d Flex Dollars as discount. Payment fully covered by Flex Dollars. Remaining Flex Dollar balance: %d.",
                totalCost, flexDollarDiscount, updatedBalance
            );
        } else {
            paymentSummary = String.format(
                "Total: $%.2f. No Flex Dollars applied. Charged $%.2f to your %s.",
                totalCost, finalAmount, paymentMethod
            );
        }
        response.put("paymentSummary", paymentSummary);
        response.put("message", "Payment processed successfully");

        return response;
    }
}

