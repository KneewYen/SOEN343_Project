package org.ridewithus.infrastructure.repository;

import org.ridewithus.domain.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TripRepository extends JpaRepository<Trip, Long> {

    Trip findByTripId(Long tripId);

    List<Trip> findByTripComplete(boolean tripComplete);

    @Query("SELECT t FROM Trip t WHERE t.reservation.user.id = :userId")
    List<Trip> findByReservationUserId(@Param("userId") Long userId);
    
    List<Trip> findByUserId(Long userId);

}