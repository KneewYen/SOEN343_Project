package org.ridewithus.domain.entity;

public class ReservedState implements BikeState {

    @Override
    public void reserve(Bike bike) throws Exception {
        throw new Exception("Bike is already Reserved");
    }

    @Override
    public void checkOut(Bike bike) {
        bike.setStatus(BikeStatus.ON_TRIP);
        bike.setDock(null); //Bike is no longer parked at that dock
        bike.changeState(new OnTripState());
    }

    @Override
    public void returnBike(Bike bike) {
        bike.setStatus(BikeStatus.AVAILABLE);
        bike.changeState(new AvailableState());
        //in case you want to delete the reservation before you checkout and within time
    }
}