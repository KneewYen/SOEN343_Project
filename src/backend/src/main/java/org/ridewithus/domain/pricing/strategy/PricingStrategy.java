package org.ridewithus.domain.pricing.strategy;

import org.ridewithus.domain.entity.Trip;

public interface PricingStrategy {
    public String getName();
    public String getDescription();
    public double calculatePrice(Trip trip);
}
