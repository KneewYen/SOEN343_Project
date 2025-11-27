package org.ridewithus.infrastructure.repository;

import org.ridewithus.domain.entity.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.ridewithus.domain.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface TripRepository extends JpaRepository<Trip, Long> {

    Trip findByTripId(Long tripId);

    List<Trip> findByTripComplete(boolean tripComplete);

    @Query("SELECT t FROM Trip t WHERE t.reservation.user.id = :userId")
    List<Trip> findByReservationUserId(@Param("userId") Long userId);
    
    List<Trip> findByUserId(Long userId);

    Page<Trip> findAll(Pageable pageable);

    Page<Trip> findByUserId(Long userId, Pageable pageable);

    List<Trip> findByUserIdAndStartTimeAfterAndTripCompleteTrue(Long userId, LocalDateTime starTime);
  
    List<Trip> findByUserIdAndTripComplete(Long userId, boolean tripComplete);

}