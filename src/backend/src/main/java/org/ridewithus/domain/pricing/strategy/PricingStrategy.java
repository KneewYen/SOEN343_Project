package org.ridewithus.domain.pricing.strategy;

import org.ridewithus.domain.entity.Trip;

public interface PricingStrategy {
    public double calculatePrice(Trip trip);
}
