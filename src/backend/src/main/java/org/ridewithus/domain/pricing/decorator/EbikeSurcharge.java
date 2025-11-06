package org.ridewithus.domain.pricing.decorator;

import org.ridewithus.domain.entity.Trip;
import org.ridewithus.domain.pricing.strategy.PricingStrategy;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EbikeSurcharge extends PricingDecorator {
    private double surcharge = 5;

    public EbikeSurcharge(PricingStrategy wrappee){
        super(wrappee);
    }

    @Override
    public double calculatePrice(Trip trip){
        return super.calculatePrice(null) + surcharge;
    }

    public String getName(){
        return "Surcharge";
    }

    public String getDescription(){
        return "e-bike";
    }
}
