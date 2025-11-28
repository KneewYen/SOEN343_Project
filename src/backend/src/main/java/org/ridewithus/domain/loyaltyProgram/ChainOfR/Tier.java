package org.ridewithus.domain.loyaltyProgram.ChainOfR;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum Tier {
    NONE(0, 0.0, 0),
    BRONZE(1, 0.05, 0),
    SILVER(2, 0.1, 2),
    GOLD(3, 0.15, 5);

    private final int value;
    private final double discount;
    private final int additionalReservationHoldTime;

    Tier(int value, double discount, int additionalReservationHoldTime) {
        this.value = value;
        this.discount = discount;
        this.additionalReservationHoldTime = additionalReservationHoldTime;
    }

    @JsonValue
    public int getValue() {
        return value;
    }
}