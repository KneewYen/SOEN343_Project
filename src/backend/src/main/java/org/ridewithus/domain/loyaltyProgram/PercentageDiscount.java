package org.ridewithus.domain.loyaltyProgram;

import org.ridewithus.domain.entity.Trip;
import org.ridewithus.domain.pricing.decorator.PricingDecorator;
import org.ridewithus.domain.pricing.strategy.PricingStrategy;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PercentageDiscount extends PricingDecorator {
    private int discount;

    public PercentageDiscount(PricingStrategy wrappee, int percentage_off){
        super(wrappee);
        discount = percentage_off ;
    }

    @Override
    public double calculatePrice(Trip trip){
        return super.calculatePrice(trip) - super.calculatePrice(trip)*discount;
    }

    public String getName(){
        return "Loyalty discount";
    }

    public String getDescription(){
        return "percentage discount for youe loyalty";
    }
}
