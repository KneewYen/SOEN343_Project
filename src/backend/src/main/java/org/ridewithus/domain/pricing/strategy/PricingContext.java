package org.ridewithus.domain.pricing.strategy;

import org.ridewithus.domain.entity.Trip;

public class PricingContext {
    
    private PricingStrategy strategy;

    public PricingContext(){
        strategy = null;
    }

    public void setStrategy(PricingStrategy strategy){
        this.strategy = strategy;
    }

    public double calculatePrice(Trip trip){
        return strategy.calculatePrice(trip);
    }
}
