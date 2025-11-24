package org.ridewithus.domain.loyaltyProgram.ChainOfR;

import org.ridewithus.domain.entity.User;
import org.ridewithus.domain.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SilverHandler implements TierHandler{
    private TierHandler next;

    @Autowired
    private UserService userService;

    public Tier handle(User user){
        boolean check1 = userService.hasAmountReservationInYear(user, 5);
        boolean check2 = userService.hasXtripPerMonthForXMonths(user, 5, 3);
        

        if (check1 && check2 ){
            return next.handle(user);
        }

        return Tier.BRONZE;

    }

    @Override
    public void setNext(TierHandler next) {
        this.next = next;
    }
}
