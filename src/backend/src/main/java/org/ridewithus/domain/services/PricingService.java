package org.ridewithus.domain.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ridewithus.domain.dto.ChargeBreakdownDTO;
import org.ridewithus.domain.dto.ChargeDTO;
import org.ridewithus.domain.entity.Bike;
import org.ridewithus.domain.entity.PricingPlan;
import org.ridewithus.domain.entity.Trip;
import org.ridewithus.domain.entity.User;
import java.util.Optional;
import org.ridewithus.domain.pricing.decorator.EbikeSurcharge;
import org.ridewithus.domain.pricing.strategy.BaseRateStrategy;
import org.ridewithus.domain.pricing.strategy.DistanceStrategy;
import org.ridewithus.domain.pricing.strategy.PricingContext;
import org.ridewithus.domain.pricing.strategy.PricingStrategy;
import org.ridewithus.infrastructure.repository.BikeRepository;
import org.ridewithus.infrastructure.repository.PricingPlanRepository;
import org.ridewithus.infrastructure.repository.TripRepository;
import org.ridewithus.infrastructure.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

    public List<PricingPlan> getAllPlans() {
        return pricingPlanRepository.findAll();
    }

    @Transactional
    public double calculatePricingPlan(Long tripId){

        PricingContext context = new PricingContext();

        // get user plan 
        Trip trip = tripRepository.findByTripId(tripId);
        User user = trip.getUser();
        PricingPlan pricingPlan = user.getPricingPlan();
        String plan = pricingPlan.getName();

        List<ChargeDTO> items = new ArrayList<>();
        
        
        PricingStrategy strategy;
        switch (plan) {
            case "Standard Plan":
                strategy = new BaseRateStrategy();
                items.add(new ChargeDTO(strategy.getName(), strategy.getDescription(), strategy.calculatePrice(trip)));
                break;
            case "Distance Plan":
                strategy = new DistanceStrategy();
                items.add(new ChargeDTO(strategy.getName(), strategy.getDescription(), strategy.calculatePrice(trip)));
                break;
            default:
                strategy = new BaseRateStrategy();
                break;
        }
        
        // Long bikeId = trip.getBikeId();
        // Bike bike = bikeRepository.findByBikeId(bikeId);

        Bike bike = trip.getBike();

        // if an e-bike, add the ebike surcharge decorator
        if(bike.getType().equals("e-bike")){
            strategy = new EbikeSurcharge(strategy);
            items.add(new ChargeDTO(strategy.getName(), strategy.getDescription(), ((EbikeSurcharge)strategy).getSurcharge() ));
        }

        context.setStrategy(strategy);
        return context.calculatePrice(trip);
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
