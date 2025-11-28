package org.ridewithus.domain.loyaltyProgram.ChainOfR;

import org.ridewithus.domain.entity.User;

public class BaseHandler implements TierHandler {
    
    private TierHandler next;

    @Override
    public void setNext(TierHandler next){
        this.next = next;
    }

    // TO-DO: is this class needed ?
    public Tier handle(User user){
        if (this.next != null){
            return next.handle(user);
        }
        return Tier.NONE;
    }
}
