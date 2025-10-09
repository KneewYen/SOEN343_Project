package org.ridewithus.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "rides")
public class Ride {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id", nullable = false)
    @NotNull(message = "Driver is required")
    private User driver;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "passenger_id", nullable = false)
    @NotNull(message = "Passenger is required")
    private User passenger;
    
    @NotBlank(message = "Pickup location is required")
    @Column(name = "pickup_location", nullable = false)
    private String pickupLocation;
    
    @NotBlank(message = "Destination is required")
    @Column(nullable = false)
    private String destination;
    
    @DecimalMin(value = "0.0", inclusive = false, message = "Fare must be greater than 0")
    @Column(precision = 10, scale = 2)
    private BigDecimal fare;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RideStatus status = RideStatus.REQUESTED;
    
    @Column(name = "estimated_duration")
    private Integer estimatedDuration; // in minutes
    
    @Column(name = "actual_duration")
    private Integer actualDuration; // in minutes
    
    @Column(name = "distance_km")
    private Double distanceKm;
    
    @Column(name = "pickup_time")
    private LocalDateTime pickupTime;
    
    @Column(name = "dropoff_time")
    private LocalDateTime dropoffTime;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Constructors
    public Ride() {}
    
    public Ride(User driver, User passenger, String pickupLocation, String destination, BigDecimal fare) {
        this.driver = driver;
        this.passenger = passenger;
        this.pickupLocation = pickupLocation;
        this.destination = destination;
        this.fare = fare;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public User getDriver() {
        return driver;
    }
    
    public void setDriver(User driver) {
        this.driver = driver;
    }
    
    public User getPassenger() {
        return passenger;
    }
    
    public void setPassenger(User passenger) {
        this.passenger = passenger;
    }
    
    public String getPickupLocation() {
        return pickupLocation;
    }
    
    public void setPickupLocation(String pickupLocation) {
        this.pickupLocation = pickupLocation;
    }
    
    public String getDestination() {
        return destination;
    }
    
    public void setDestination(String destination) {
        this.destination = destination;
    }
    
    public BigDecimal getFare() {
        return fare;
    }
    
    public void setFare(BigDecimal fare) {
        this.fare = fare;
    }
    
    public RideStatus getStatus() {
        return status;
    }
    
    public void setStatus(RideStatus status) {
        this.status = status;
    }
    
    public Integer getEstimatedDuration() {
        return estimatedDuration;
    }
    
    public void setEstimatedDuration(Integer estimatedDuration) {
        this.estimatedDuration = estimatedDuration;
    }
    
    public Integer getActualDuration() {
        return actualDuration;
    }
    
    public void setActualDuration(Integer actualDuration) {
        this.actualDuration = actualDuration;
    }
    
    public Double getDistanceKm() {
        return distanceKm;
    }
    
    public void setDistanceKm(Double distanceKm) {
        this.distanceKm = distanceKm;
    }
    
    public LocalDateTime getPickupTime() {
        return pickupTime;
    }
    
    public void setPickupTime(LocalDateTime pickupTime) {
        this.pickupTime = pickupTime;
    }
    
    public LocalDateTime getDropoffTime() {
        return dropoffTime;
    }
    
    public void setDropoffTime(LocalDateTime dropoffTime) {
        this.dropoffTime = dropoffTime;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public enum RideStatus {
        REQUESTED, ACCEPTED, IN_PROGRESS, COMPLETED, CANCELLED
    }
}

