package org.ridewithus.domain.entity;

public class MaintenanceState implements BikeState {

    @Override
    public void reserve(Bike bike) throws Exception {
        throw new Exception("Cannot reserve a bike that is under maintenance");
    }

    @Override
    public void checkOut(Bike bike) throws Exception {
        throw new Exception("Cannot checkout a bike that is under maintenance");
    }

    @Override
    public void returnBike(Bike bike) throws Exception {
        throw new Exception("Cannot return a bike that is under maintenance");
    }
}
