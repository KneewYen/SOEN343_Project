package org.ridewithus.domain.pricing.decorator;

import org.ridewithus.domain.entity.Trip;
import org.ridewithus.domain.pricing.strategy.PricingStrategy;

public class EbikeSurcharge extends PricingDecorator {
    private double surcharge = 5;

    public EbikeSurcharge(PricingStrategy wrappee){
        super(wrappee);
    }

    @Override
    public double calculatePrice(Trip trip){
        return super.calculatePrice(null) + surcharge;
    }
}
