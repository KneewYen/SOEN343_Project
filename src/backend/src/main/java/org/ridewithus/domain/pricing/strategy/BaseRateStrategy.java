package org.ridewithus.domain.pricing.strategy;

import java.time.Duration;

import org.ridewithus.domain.entity.Trip;

public class BaseRateStrategy implements PricingStrategy {
    
    private double baseRate = 10;
    private double minuteRate = 1;
    @Override
    public double calculatePrice(Trip trip){

        Duration duration = Duration.between(trip.getStartTime(), trip.getEndTime());
        long minutes = duration.toMinutes(); 

        double price = baseRate + minutes*minuteRate;

        return price;
    }

    public String getName(){
        return "Base plan";
    }

    public String getDescription(){
        return "Per-minute plan (1$/min)";
    }
}
