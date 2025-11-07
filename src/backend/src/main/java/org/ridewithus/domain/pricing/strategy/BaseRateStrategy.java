package org.ridewithus.domain.pricing.strategy;

import java.time.Duration;

import org.ridewithus.domain.entity.Trip;

public class BaseRateStrategy implements PricingStrategy {
    
    private double baseRate = 5;
    private double minuteRate = 1;
    @Override
    public double calculatePrice(Trip trip){

        Duration duration = Duration.between(trip.getStartTime(), trip.getEndTime());
        long minutes = duration.toMinutes(); 

        double price = baseRate + minutes*minuteRate;

        System.out.println("distance"+minutes);
        System.out.println("price5"+price);

        return price;
    }

    public String getName(){
        return "Base plan";
    }

    public String getDescription(){
        return "Per-minute plan (1$/min)";
    }
}
