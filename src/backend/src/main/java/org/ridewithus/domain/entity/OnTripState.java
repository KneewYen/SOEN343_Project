package org.ridewithus.domain.entity;

public class OnTripState implements BikeState {

    @Override
    public void reserve(Bike bike) throws Exception {
        throw new Exception("Cannot reserve a bike that is on a trip");
    }

    @Override
    public void checkOut(Bike bike) throws Exception {
        throw new Exception("Cannot checkout a bike that is on a trip");
    }

    @Override
    public void returnBike(Bike bike) {
        bike.setStatus(BikeStatus.AVAILABLE);
        bike.changeState(new AvailableState());
    }
}
