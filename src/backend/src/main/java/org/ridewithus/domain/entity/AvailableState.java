package org.ridewithus.domain.entity;

public class AvailableState implements BikeState {

    @Override
    public void reserve(Bike bike) {
        bike.setStatus(BikeStatus.RESERVED);
        bike.changeState(new ReservedState());
    }

    @Override
    public void checkOut(Bike bike) throws Exception {
        throw new Exception("Cannot checkout a bike without a reservation");
    }

    @Override
    public void returnBike(Bike bike) throws Exception {
        throw new Exception("Cannot return a bike that has not been checked out");
    }
}
