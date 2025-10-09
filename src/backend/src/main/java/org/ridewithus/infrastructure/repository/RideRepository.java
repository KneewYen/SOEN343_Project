package org.ridewithus.infrastructure.repository;

import org.ridewithus.domain.entity.Ride;
import org.ridewithus.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RideRepository extends JpaRepository<Ride, Long> {
    
    // Find rides by driver
    List<Ride> findByDriver(User driver);
    
    // Find rides by passenger
    List<Ride> findByPassenger(User passenger);
    
    // Find rides by status
    List<Ride> findByStatus(Ride.RideStatus status);
    
    // Find rides by driver and status
    List<Ride> findByDriverAndStatus(User driver, Ride.RideStatus status);
    
    // Find rides by passenger and status
    List<Ride> findByPassengerAndStatus(User passenger, Ride.RideStatus status);
    
    // Find rides within date range
    List<Ride> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    // Find rides by pickup location containing
    List<Ride> findByPickupLocationContainingIgnoreCase(String location);
    
    // Find rides by destination containing
    List<Ride> findByDestinationContainingIgnoreCase(String destination);
    
    // Custom query to find active rides for a driver
    @Query("SELECT r FROM Ride r WHERE r.driver = :driver AND r.status IN ('ACCEPTED', 'IN_PROGRESS')")
    List<Ride> findActiveRidesForDriver(@Param("driver") User driver);
    
    // Custom query to find active rides for a passenger
    @Query("SELECT r FROM Ride r WHERE r.passenger = :passenger AND r.status IN ('REQUESTED', 'ACCEPTED', 'IN_PROGRESS')")
    List<Ride> findActiveRidesForPassenger(@Param("passenger") User passenger);
    
    // Custom query to find rides by fare range
    @Query("SELECT r FROM Ride r WHERE r.fare BETWEEN :minFare AND :maxFare")
    List<Ride> findByFareRange(@Param("minFare") Double minFare, @Param("maxFare") Double maxFare);
    
    // Custom query to find completed rides for a user (as driver or passenger)
    @Query("SELECT r FROM Ride r WHERE (r.driver = :user OR r.passenger = :user) AND r.status = 'COMPLETED'")
    List<Ride> findCompletedRidesForUser(@Param("user") User user);
    
    // Custom query to find rides by distance range
    @Query("SELECT r FROM Ride r WHERE r.distanceKm BETWEEN :minDistance AND :maxDistance")
    List<Ride> findByDistanceRange(@Param("minDistance") Double minDistance, @Param("maxDistance") Double maxDistance);
}

