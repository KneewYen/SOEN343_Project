package org.ridewithus.infrastructure.repository;

import org.ridewithus.domain.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TripRepository extends JpaRepository<Trip, Long> {

    Trip findByTripId(Long tripId);

    List<Trip> findByTripComplete(boolean tripComplete);

}