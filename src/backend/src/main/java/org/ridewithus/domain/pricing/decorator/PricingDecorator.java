package org.ridewithus.domain.pricing.decorator;

import org.ridewithus.domain.entity.Trip;
import org.ridewithus.domain.pricing.strategy.PricingStrategy;

public abstract class PricingDecorator implements PricingStrategy {
    
    private PricingStrategy wrappee;

    public PricingDecorator(PricingStrategy strategy){
        wrappee = strategy;
    }

    @Override
    public double calculatePrice(Trip trip){
        return wrappee.calculatePrice(trip);
    }
}
