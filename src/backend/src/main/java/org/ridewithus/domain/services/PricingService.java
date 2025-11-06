package org.ridewithus.domain.services;

import java.util.ArrayList;
import java.util.List;

import org.ridewithus.domain.dto.ChargeBreakdownDTO;
import org.ridewithus.domain.dto.ChargeDTO;
import org.ridewithus.domain.entity.Bike;
import org.ridewithus.domain.entity.PricingPlan;
import org.ridewithus.domain.entity.Trip;
import org.ridewithus.domain.entity.User;
import org.ridewithus.domain.pricing.decorator.EbikeSurcharge;
import org.ridewithus.domain.pricing.strategy.BaseRateStrategy;
import org.ridewithus.domain.pricing.strategy.DistanceStrategy;
import org.ridewithus.domain.pricing.strategy.PricingContext;
import org.ridewithus.domain.pricing.strategy.PricingStrategy;
import org.ridewithus.infrastructure.repository.BikeRepository;
import org.ridewithus.infrastructure.repository.PricingPlanRepository;
import org.ridewithus.infrastructure.repository.TripRepository;
import org.ridewithus.infrastructure.repository.UserRepository;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Service
public class PricingService {
    
    private TripRepository tripRepository;
    private BikeRepository bikeRepository;
    private UserRepository userRepository;
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
}
