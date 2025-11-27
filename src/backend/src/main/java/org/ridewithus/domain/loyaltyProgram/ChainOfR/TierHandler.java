package org.ridewithus.domain.loyaltyProgram.ChainOfR;

import org.ridewithus.domain.entity.User;

public interface TierHandler {
    void setNext(TierHandler next);

    Tier handle(User user);
}
