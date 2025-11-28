package org.ridewithus.domain.services;

import org.ridewithus.domain.entity.Station;
import org.ridewithus.domain.entity.Trip;
import org.ridewithus.domain.entity.User;

public class FlexDollars {

    private double balance;
    private Station station;
    private User user;
    private Trip trip;

    public FlexDollars(double initialBalance) {
        this.balance = initialBalance;
    }

    public double getBalance() {
        return balance;
    }

    public void applyFlexDollars(double amount) {
        if (amount <= balance) {
            balance -= amount;
        } else {
            throw new IllegalArgumentException("Insufficient FlexDollars balance.");
        }
    }
    public boolean isFlexDollarsApplyable(double rideCost) {
        return balance >= rideCost;
    }


   public double calculateBalanceAfterRide(double rideCost) {
     return balance;
    }
}
