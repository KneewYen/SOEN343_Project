package org.ridewithus.domain.services;

import org.ridewithus.domain.entity.Station;
import org.ridewithus.domain.entity.Trip;
import org.ridewithus.domain.entity.User;

public interface FlexDollar {

    public Station getStationById(Long id);
    public Trip getTripById(Long id);
    public Station getStationCapacity(Long id);
    public User getUserById(Long id);

}
