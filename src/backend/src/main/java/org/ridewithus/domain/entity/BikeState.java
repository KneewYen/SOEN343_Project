package org.ridewithus.domain.entity;

public interface BikeState {

    void reserve(Bike bike) throws Exception;

    void checkOut(Bike bike) throws Exception;

    void returnBike(Bike bike) throws Exception;

}
