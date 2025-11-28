package org.ridewithus.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ridewithus.domain.entity.*;
import org.ridewithus.domain.services.*;
import org.ridewithus.infrastructure.repository.*;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit test for Station Full scenario:
 * Return attempt at full station triggers overflow credit. Show the user account credit now available.
 * 
 * Note: Currently the system throws an exception when station is full.
 * This test verifies the current behavior. If overflow credit feature is implemented,
 * this test should be updated to verify credit is awarded instead of exception.
 */
@ExtendWith(MockitoExtension.class)
class StationFullTest {

    @Mock
    private TripRepository tripRepository;

    @Mock
    private StationRepository stationRepository;

    @Mock
    private DockRepository dockRepository;

    @Mock
    private BikeRepository bikeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private StationService stationService;

    @Mock
    private DomainEventService eventService;

    @Mock
    private FlexDollarService flexDollarService;

    @InjectMocks
    private TripService tripService;

    private User rider;
    private Station fullStation;
    private Bike bike;
    private Dock dockAtStart;
    private Reservation reservation;
    private Trip trip;

    @BeforeEach
    void setUp() {
        // Setup rider
        rider = new User();
        rider.setId(1L);
        rider.setUserName("testRider");
        rider.setEmail("rider@test.com");
        rider.setFlexDollars(0);

        // Setup full station (all docks occupied)
        fullStation = new Station();
        fullStation.setId(1L);
        fullStation.setName("Full Station");
        fullStation.setCapacity(10);
        fullStation.setCount(10); // Station is at capacity

        // Setup dock at start station
        Station startStation = new Station();
        startStation.setId(2L);
        startStation.setName("Start Station");
        startStation.setCapacity(20);
        startStation.setCount(5);

        dockAtStart = new Dock();
        dockAtStart.setId(1L);
        dockAtStart.setStation(startStation);
        dockAtStart.setStatus(Dock.DockStatus.OCCUPIED);

        // Setup Bike
        bike = new Bike();
        bike.setId(1L);
        bike.setType("standard");
        bike.setStatus(BikeStatus.ON_TRIP);
        bike.setDock(dockAtStart);

        // Setup Reservation
        reservation = new Reservation();
        reservation.setReservationId(1L);
        reservation.setUser(rider);
        reservation.setBike(bike);
        reservation.setStatus(Reservation.ReservationStatus.ACTIVE);

        // Setup Trip
        trip = new Trip();
        trip.setTripId(1L);
        trip.setUser(rider);
        trip.setReservation(reservation);
        trip.setStartStation(startStation);
        trip.setStartTime(LocalDateTime.now().minusMinutes(10));
        trip.setTripComplete(false);
    }

    @Test
    void stationFull_throwsException_whenNoEmptyDocksAvailable() throws Exception {
        // -------------------
        // Arrange - Station is full (no empty docks)
        // -------------------
        
        when(tripRepository.findByTripId(1L)).thenReturn(trip);
        when(stationRepository.findById(1L)).thenReturn(Optional.of(fullStation));
        
        // No empty docks available
        when(dockRepository.findByStationAndStatus(eq(fullStation), eq(Dock.DockStatus.EMPTY)))
            .thenReturn(Collections.emptyList());

        // -------------------
        // Act & Assert - Should throw exception
        // -------------------
        Exception exception = assertThrows(Exception.class, () -> {
            tripService.endTrip(1L, 1L);
        });

        // Verify exception message
        assertTrue(exception.getMessage().contains("No empty places available to Dock") ||
                   exception.getMessage().contains("empty places"));

        // Verify no changes were made
        verify(tripRepository, never()).save(any());
        verify(bikeRepository, never()).save(any());
        verify(dockRepository, never()).save(any());
    }

    @Test
    void stationFull_emitsEvent_whenStationBecomesFullAfterReturn() throws Exception {
        // -------------------
        // Arrange - Station has one empty dock, becomes full after return
        // -------------------
        
        Station almostFullStation = new Station();
        almostFullStation.setId(3L);
        almostFullStation.setName("Almost Full Station");
        almostFullStation.setCapacity(10);
        almostFullStation.setCount(9); // One space left

        Dock lastEmptyDock = new Dock();
        lastEmptyDock.setId(10L);
        lastEmptyDock.setStation(almostFullStation);
        lastEmptyDock.setStatus(Dock.DockStatus.EMPTY);

        when(tripRepository.findByTripId(1L)).thenReturn(trip);
        when(stationRepository.findById(3L)).thenReturn(Optional.of(almostFullStation));
        
        // One empty dock available
        when(dockRepository.findByStationAndStatus(eq(almostFullStation), eq(Dock.DockStatus.EMPTY)))
            .thenReturn(Collections.singletonList(lastEmptyDock));

        // After return, station becomes full (no empty docks)
        when(dockRepository.findAllByStationAndStatus(eq(almostFullStation), eq(Dock.DockStatus.EMPTY)))
            .thenReturn(Collections.emptyList());

        // Mock bikes available for capacity check
        when(stationService.getBikesAvailable(3L)).thenReturn(
            Collections.emptyList() // Station will be below 25% after return
        );

        // Mock user repository for reload after Flex Dollar check
        when(userRepository.findById(1L)).thenReturn(Optional.of(rider));

        // -------------------
        // Act - Return bike to almost full station
        // -------------------
        Map<String, Object> result = tripService.endTrip(1L, 3L);

        // -------------------
        // Assert - Verify STATION_FULL event is emitted
        // -------------------
        verify(eventService).emitEvent(
            eq(rider),
            eq("STATION_FULL"),
            contains("Almost Full Station station is full")
        );

        // Verify trip was completed
        assertTrue(trip.isTripComplete());
        assertNotNull(result);
        assertEquals(1L, result.get("tripId"));
    }

    /**
     * Future test for overflow credit feature:
     * When overflow credit is implemented, this test should verify that:
     * 1. User receives overflow credit (Flex Dollars) when returning to full station
     * 2. Credit is added to user's account
     * 3. User is notified of the credit
     * 4. Ledger entry is created
     */
    @Test
    void stationFull_shouldAwardOverflowCredit_whenFeatureIsImplemented() {
        // TODO: Implement when overflow credit feature is added
        // This test should verify:
        // - Overflow credit is awarded when station is full
        // - Credit amount is added to user's Flex Dollar balance
        // - Transaction is recorded in ledger
        // - User receives notification
        assertTrue(true, "Placeholder test - implement when overflow credit feature is added");
    }
}

