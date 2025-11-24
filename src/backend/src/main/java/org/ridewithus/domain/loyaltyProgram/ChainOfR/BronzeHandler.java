package org.ridewithus.domain.loyaltyProgram.ChainOfR;

import org.ridewithus.domain.entity.User;
import org.ridewithus.domain.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BronzeHandler implements TierHandler {
    private TierHandler next;

    @Autowired
    private UserService userService;

    public Tier handle(User user){
        boolean check1 = userService.hasMissedReservations(user);
        boolean check2 = userService.returnAllBikesForLife(user);
        boolean check3 = userService.hasCompletedAmountTripsInYear(user, 10);

        if (!check1 && check2 && check3){
            return next.handle(user);
        }

        return Tier.NONE;

    }

    @Override
    public void setNext(TierHandler next) {
        this.next = next;
    }
}
