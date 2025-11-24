package org.ridewithus.domain.loyaltyProgram.ChainOfR;

import org.ridewithus.domain.entity.User;
import org.ridewithus.domain.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GoldHandler implements TierHandler{
    private TierHandler next;  // last tier so no handler

    @Autowired
    private UserService userService;

    public Tier handle(User user){
        boolean check1 = userService.hasXtripPerMonthForXMonths(user, 5, 3);
        

        if (check1){
            return Tier.GOLD;
        }

        return Tier.SILVER;

    }

    @Override
    public void setNext(TierHandler next) {
        this.next = next;
    }
}
