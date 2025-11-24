package org.ridewithus.domain.services;

import java.util.ArrayList;
import java.util.List;

import org.ridewithus.domain.dto.BillingDTO;

import org.ridewithus.domain.dto.ChargeDTO;
import org.ridewithus.domain.entity.Bike;
import org.ridewithus.domain.entity.PricingPlan;
import org.ridewithus.domain.entity.Trip;
import org.ridewithus.domain.entity.User;
import java.util.Optional;

import org.ridewithus.domain.loyaltyProgram.PercentageDiscount;
import org.ridewithus.domain.pricing.decorator.EbikeSurcharge;
import org.ridewithus.domain.pricing.strategy.BaseRateStrategy;
import org.ridewithus.domain.pricing.strategy.DistanceStrategy;
import org.ridewithus.domain.pricing.strategy.PricingContext;
import org.ridewithus.domain.pricing.strategy.PricingStrategy;
import org.ridewithus.infrastructure.repository.BikeRepository;
import org.ridewithus.infrastructure.repository.PricingPlanRepository;
import org.ridewithus.infrastructure.repository.TripRepository;
import org.ridewithus.infrastructure.repository.UserRepository;
import org.ridewithus.infrastructure.repository.BillingRepository;
import org.ridewithus.infrastructure.repository.ChargeRepository;
import org.ridewithus.domain.entity.Billing;
import org.ridewithus.domain.entity.Charge;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Service
public class PricingService {
    
    @Autowired
    private TripRepository tripRepository;
    @Autowired
    private BikeRepository bikeRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PricingPlanRepository pricingPlanRepository;
    @Autowired
    private BillingRepository billingRepository;
    @Autowired
    private ChargeRepository chargeRepository;

    public List<PricingPlan> getAllPlans() {
        return pricingPlanRepository.findAll();
    }

    @Transactional
    public BillingDTO calculatePricingPlan(Long tripId){

        PricingContext context = new PricingContext();

        // get user plan 
        Trip trip = tripRepository.findByTripId(tripId);
        User user = trip.getUser();
        PricingPlan pricingPlan = user.getPricingPlan();
        String plan = pricingPlan.getName();

        List<ChargeDTO> items = new ArrayList<>();
        
        PricingStrategy strategy;

        PercentageDiscount percentageDiscount;
        switch (plan) {
            case "Standard plan":
                strategy = new BaseRateStrategy();
                percentageDiscount = new PercentageDiscount(strategy, user.getPrevLoyaltyTier().getDiscount());
                items.add(new ChargeDTO(strategy.getName(), strategy.getDescription(), percentageDiscount.calculatePrice(trip)));
                break;
            case "Distance plan":
                strategy = new DistanceStrategy();
                percentageDiscount = new PercentageDiscount(strategy, user.getPrevLoyaltyTier().getDiscount());
                items.add(new ChargeDTO(strategy.getName(), strategy.getDescription(), percentageDiscount.calculatePrice(trip)));
                break;
            default:
                strategy = new BaseRateStrategy();
                percentageDiscount = new PercentageDiscount(strategy, user.getPrevLoyaltyTier().getDiscount());
                items.add(new ChargeDTO(strategy.getName(), strategy.getDescription(), percentageDiscount.calculatePrice(trip)));
                break;
        }
        
        // Long bikeId = trip.getBikeId();
        // Bike bike = bikeRepository.findByBikeId(bikeId);

        Bike bike = trip.getBike();

        // if an e-bike, add the ebike surcharge decorator
        if(bike.getType().equals("e-bike")){
            System.out.println("bikela"+bike.getType());
            strategy = new EbikeSurcharge(strategy);
            items.add(new ChargeDTO(strategy.getName(), strategy.getDescription(), ((EbikeSurcharge)strategy).getSurcharge() * (1 - percentageDiscount.getDiscount())));
        }
        
        // Check if billing already exists for this trip
        Billing billing = billingRepository.findByTrip(trip).orElse(null);
        
        if (billing == null) {
            // Create Billing object
            billing = Billing.builder()
                    .trip(trip)
                    .charges(new ArrayList<>())
                    .build();
            
            // Save Billing to database first to get the ID
            billing = billingRepository.save(billing);
            
            // Create Charge objects from ChargeDTOs and link them to Billing
            List<Charge> charges = new ArrayList<>();
            for (ChargeDTO chargeDTO : items) {
                Charge charge = Charge.builder()
                        .name(chargeDTO.getName())
                        .description(chargeDTO.getDescription())
                        .cost(chargeDTO.getCost())
                        .billing(billing)
                        .build();
                charges.add(charge);
            }
            
            // Save all Charge objects to database
            charges = chargeRepository.saveAll(charges);
            
            // Update billing with charges and save again
            billing.setCharges(charges);
            billing = billingRepository.save(billing);
        }
        
        context.setStrategy(strategy);
        double price = context.calculatePrice(trip);

        BillingDTO billingDTO = BillingDTO.builder()
            .billingId(billing.getBillingId())
            .tripId(trip.getTripId())
            .charges(items)
            .totalAmount(price)
            .build();

        user.setPrevLoyaltyTier(user.getLoyaltyTier());
        userRepository.save(user);

        return billingDTO;
    }

    @Transactional
    public void assignPlanToUser(Long userId, Long planId) throws Exception{
        
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            System.out.println("user is void!!"+userId);
            throw new Exception("User not found");
        }

        User user = userOptional.get();

        Optional<PricingPlan> plans = pricingPlanRepository.findById(planId);
        PricingPlan plan = plans.get();

        System.out.println("Planet" +plan);

        user.setPricingPlan(plan);
        userRepository.save(user);
    }
}
