package org.ridewithus.infrastructure.repository;

import org.ridewithus.domain.entity.Billing;
import org.ridewithus.domain.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BillingRepository extends JpaRepository<Billing, Long> {
    Optional<Billing> findByTrip(Trip trip);

    @Query("SELECT b FROM Billing b LEFT JOIN FETCH b.charges WHERE b.trip.tripId = :tripId")
    Optional<Billing> findByTrip_TripId(@Param("tripId")Long tripId);
    List<Billing> findByTrip_User_Id(Long userId);
}

