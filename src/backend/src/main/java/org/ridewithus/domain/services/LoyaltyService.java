package org.ridewithus.domain.services;

import org.ridewithus.domain.entity.User;
import org.ridewithus.domain.loyaltyProgram.ChainOfR.BronzeHandler;
import org.ridewithus.domain.loyaltyProgram.ChainOfR.GoldHandler;
import org.ridewithus.domain.loyaltyProgram.ChainOfR.SilverHandler;
import org.ridewithus.domain.loyaltyProgram.ChainOfR.Tier;
import org.ridewithus.infrastructure.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Service
public class LoyaltyService {
    @Autowired 
    private UserRepository userRepository;

    @Autowired
    private BronzeHandler bronzeHandler;

    @Autowired
    private SilverHandler silverHandler;

    @Autowired
    private GoldHandler goldHandler;

    @Transactional
    public void updateLoyaltyTier(User user){
        // TierHandler bronzeHandler = new BronzeHandler();
        // TierHandler silverHandler = new SilverHandler();
        // TierHandler goldHandler = new GoldHandler();

        bronzeHandler.setNext(silverHandler);
        silverHandler.setNext(goldHandler);

        Tier updatedTier = bronzeHandler.handle(user);
        Tier currentTier = user.getLoyaltyTier();

        // only update if the tier changes
        if (updatedTier != currentTier){
            user.setLoyaltyTier(updatedTier);
            user.setPrevLoyaltyTier(currentTier);
            userRepository.save(user);
        }
    }
}
