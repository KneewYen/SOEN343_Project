package org.ridewithus.domain.loyaltyProgram.ChainOfR;

import org.ridewithus.domain.entity.User;

public interface TierHandler {
    public void setNext(TierHandler next);

    public Tier handle(User user);
}
