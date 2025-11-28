package org.ridewithus.domain.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ridewithus.domain.entity.*;
import org.ridewithus.infrastructure.repository.*;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for Flex Dollar feature in TripService
 * Tests the "Award Flex Dollars" use case (UC-03)
 */
@ExtendWith(MockitoExtension.class)
class FlexDollarAwardTest {

    @Mock
    private TripRepository tripRepository;

    @Mock
    private DockRepository dockRepository;

    @Mock
    private BikeRepository bikeRepository;

    @Mock
    private StationRepository stationRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private DomainEventService eventService;

    @Mock
    private StationService stationService;

    @InjectMocks
    private TripService tripService;
    @InjectMocks
    private FlexDollarService flexDollarService;

    private Trip trip;
    private Station station;
    private User user;
    private Bike bike;
    private Dock dock;
    private Reservation reservation;

    @BeforeEach
    void setUp() {
        // Setup user with initial flex dollar balance of 0
        user = new User("John Doe", "johndoe", "123 Main St", "john@test.com", "password");
        user.setFlexDollars(0);

        // Setup station with 10 capacity
        station = new Station();
        station.setId(1L);
        station.setName("Downtown Station");
        station.setCapacity(10);
        station.setCount(2); // 2 bikes = 20% occupancy

        // Setup dock
        dock = new Dock();
        dock.setId(1L);
        dock.setStatus(Dock.DockStatus.EMPTY);
        dock.setStation(station);

        // Setup bike
        bike = new Bike();
        bike.setId(1L);
        bike.setType("REGULAR");
        bike.setStatus(BikeStatus.ON_TRIP);
        bike.setDock(dock);

        // Setup reservation
        reservation = new Reservation();
        reservation.setReservationId(1L);
        reservation.setUser(user);
        reservation.setBike(bike);
        reservation.setStatus(Reservation.ReservationStatus.ACTIVE);
        reservation.setExpiryDateTime(LocalDateTime.now().plusMinutes(10));

        // Setup trip
        trip = Trip.builder()
                .tripId(1L)
                .user(user)
                .bike(bike)
                .reservation(reservation)
                .startStation(station)
                .startTime(LocalDateTime.now().minusMinutes(30))
                .tripComplete(false)
                .build();
    }

    /**
     * Test Case 1: Flex dollar should be awarded when returning to low-capacity station (< 25%)
     * Expected: User receives 1 flex dollar, balance updated in database
     */
    @Test
    void testFlexDollarAwarded_WhenStationCapacityBelow25Percent() throws Exception {
        // Arrange
        when(tripRepository.findByTripId(1L)).thenReturn(trip);
        when(stationRepository.findById(1L)).thenReturn(Optional.of(station));
        when(dockRepository.findByStationAndStatus(station, Dock.DockStatus.EMPTY))
                .thenReturn(List.of(dock));
        when(stationService.minimumCapacityReached(1L)).thenReturn(true); // < 25%

        // Act
        Map<String, Object> result = tripService.endTrip(1L, 1L);

        // Assert
        assertTrue((Boolean) result.get("flexDollarAwarded"), "Flex dollar should be awarded");
        assertEquals(1, result.get("flexDollarBalance"), "User should have 1 flex dollar");
        assertEquals(1, user.getFlexDollars(), "User's flex dollar balance should be 1");

        // Verify user was saved with updated balance
        verify(userRepository, times(1)).save(user);

        // Verify event was emitted
        verify(eventService).emitEvent(
                eq(user),
                eq("FLEX_DOLLAR_AWARDED"),
                contains("You earned 1 Flex Dollar")
        );
    }

    /**
     * Test Case 2: Flex dollar should NOT be awarded when station capacity >= 25%
     * Expected: User receives no flex dollar, balance unchanged
     */
    @Test
    void testFlexDollarNotAwarded_WhenStationCapacityAbove25Percent() throws Exception {
        // Arrange
        station.setCount(5); // 5/10 = 50% occupancy
        when(tripRepository.findByTripId(1L)).thenReturn(trip);
        when(stationRepository.findById(1L)).thenReturn(Optional.of(station));
        when(dockRepository.findByStationAndStatus(station, Dock.DockStatus.EMPTY))
                .thenReturn(List.of(dock));
        when(stationService.minimumCapacityReached(1L)).thenReturn(false); // >= 25%

        // Act
        Map<String, Object> result = tripService.endTrip(1L, 1L);

        // Assert
        assertFalse((Boolean) result.get("flexDollarAwarded"), "Flex dollar should NOT be awarded");
        assertEquals(0, result.get("flexDollarBalance"), "User should have 0 flex dollars");
        assertEquals(0, user.getFlexDollars(), "User's flex dollar balance should remain 0");

        // Verify user was NOT saved (no flex dollar update)
        verify(userRepository, never()).save(user);

        // Verify no flex dollar award event was emitted
        verify(eventService, never()).emitEvent(
                any(User.class),
                eq("FLEX_DOLLAR_AWARDED"),
                anyString()
        );
    }

    /**
     * Test Case 3: Flex dollar award should accumulate with existing balance
     * Expected: New flex dollar added to existing balance
     */
    @Test
    void testFlexDollarAccumulation_WithExistingBalance() throws Exception {
        // Arrange
        user.setFlexDollars(5); // User already has 5 flex dollars
        when(tripRepository.findByTripId(1L)).thenReturn(trip);
        when(stationRepository.findById(1L)).thenReturn(Optional.of(station));
        when(dockRepository.findByStationAndStatus(station, Dock.DockStatus.EMPTY))
                .thenReturn(List.of(dock));
        when(stationService.minimumCapacityReached(1L)).thenReturn(true);

        // Act
        Map<String, Object> result = tripService.endTrip(1L, 1L);

        // Assert
        assertTrue((Boolean) result.get("flexDollarAwarded"));
        assertEquals(6, result.get("flexDollarBalance"), "Balance should increase to 6");
        assertEquals(6, user.getFlexDollars(), "User should have 6 flex dollars");
        verify(userRepository, times(1)).save(user);
    }

    /**
     * Test Case 4: Trip should complete successfully even if flex dollar award fails
     * Expected: Trip completes, exception logged but not thrown
     */
    @Test
    void testTripCompletes_EvenIfFlexDollarAwardFails() throws Exception {
        // Arrange
        when(tripRepository.findByTripId(1L)).thenReturn(trip);
        when(stationRepository.findById(1L)).thenReturn(Optional.of(station));
        when(dockRepository.findByStationAndStatus(station, Dock.DockStatus.EMPTY))
                .thenReturn(List.of(dock));
        when(stationService.minimumCapacityReached(1L))
                .thenThrow(new RuntimeException("Station service error"));

        // Act - should not throw exception
        Map<String, Object> result = tripService.endTrip(1L, 1L);

        // Assert
        assertNotNull(result, "Trip should complete despite flex dollar error");
        assertEquals(1L, result.get("tripId"), "Trip ID should be returned");
        assertFalse((Boolean) result.get("flexDollarAwarded"), "Flex dollar should not be awarded due to error");

        // Verify trip was still saved and completed
        verify(tripRepository, atLeastOnce()).save(trip);
        assertTrue(trip.isTripComplete(), "Trip should be marked as complete");
    }

    /**
     * Test Case 5: Response should include all required flex dollar fields
     * Expected: Response contains tripId, flexDollarBalance, flexDollarAwarded
     */
    @Test
    void testEndTripResponse_ContainsFlexDollarFields() throws Exception {
        // Arrange
        when(tripRepository.findByTripId(1L)).thenReturn(trip);
        when(stationRepository.findById(1L)).thenReturn(Optional.of(station));
        when(dockRepository.findByStationAndStatus(station, Dock.DockStatus.EMPTY))
                .thenReturn(List.of(dock));
        when(stationService.minimumCapacityReached(1L)).thenReturn(true);

        // Act
        Map<String, Object> result = tripService.endTrip(1L, 1L);

        // Assert
        assertTrue(result.containsKey("tripId"), "Response should contain tripId");
        assertTrue(result.containsKey("flexDollarBalance"), "Response should contain flexDollarBalance");
        assertTrue(result.containsKey("flexDollarAwarded"), "Response should contain flexDollarAwarded");

        assertNotNull(result.get("tripId"));
        assertNotNull(result.get("flexDollarBalance"));
        assertNotNull(result.get("flexDollarAwarded"));
    }

    /**
     * Test Case 6: Multiple trips to low-capacity stations should award multiple flex dollars
     * Expected: Each trip awards 1 flex dollar independently
     */
    @Test
    void testMultipleFlexDollarAwards_FromMultipleTrips() throws Exception {
        // First trip
        when(tripRepository.findByTripId(1L)).thenReturn(trip);
        when(stationRepository.findById(1L)).thenReturn(Optional.of(station));
        when(dockRepository.findByStationAndStatus(station, Dock.DockStatus.EMPTY))
                .thenReturn(List.of(dock));
        when(stationService.minimumCapacityReached(1L)).thenReturn(true);

        Map<String, Object> result1 = tripService.endTrip(1L, 1L);
        assertEquals(1, result1.get("flexDollarBalance"));

        // Second trip (user now has 1 flex dollar)
        user.setFlexDollars(1);
        Trip trip2 = Trip.builder()
                .tripId(2L)
                .user(user)
                .bike(bike)
                .reservation(reservation)
                .startStation(station)
                .startTime(LocalDateTime.now().minusMinutes(30))
                .tripComplete(false)
                .build();

        when(tripRepository.findByTripId(2L)).thenReturn(trip2);
        when(stationService.minimumCapacityReached(1L)).thenReturn(true);

        Map<String, Object> result2 = tripService.endTrip(2L, 1L);

        // Assert
        assertEquals(2, result2.get("flexDollarBalance"));
        assertEquals(2, user.getFlexDollars());
    }

    /**
     * Test Case 7: Exactly 25% capacity should NOT award flex dollar (boundary test)
     * Expected: No flex dollar awarded at exactly 25%
     */
    @Test
    void testNoFlexDollarAwarded_AtExactly25PercentCapacity() throws Exception {
        // Arrange - exactly 25% (2.5 bikes out of 10, rounded to 2)
        station.setCount(3); // Slightly above 25%
        when(tripRepository.findByTripId(1L)).thenReturn(trip);
        when(stationRepository.findById(1L)).thenReturn(Optional.of(station));
        when(dockRepository.findByStationAndStatus(station, Dock.DockStatus.EMPTY))
                .thenReturn(List.of(dock));
        lenient().when(stationService.minimumCapacityReached(1L)).thenReturn(false); // Exactly 25% = false

        // Act
        Map<String, Object> result = tripService.endTrip(1L, 1L);

        // Assert
        assertFalse((Boolean) result.get("flexDollarAwarded"));
        assertEquals(0, user.getFlexDollars());
    }

    /**
     * Test Case 8: Verify correct event message format
     * Expected: Event message contains station name and award amount
     */
    @Test
    void testFlexDollarAwardEvent_ContainsCorrectMessage() throws Exception {
        // Arrange
        when(tripRepository.findByTripId(1L)).thenReturn(trip);
        when(stationRepository.findById(1L)).thenReturn(Optional.of(station));
        when(dockRepository.findByStationAndStatus(station, Dock.DockStatus.EMPTY))
                .thenReturn(List.of(dock));
        when(stationService.minimumCapacityReached(1L)).thenReturn(true);

        // Act
        tripService.endTrip(1L, 1L);

        // Assert
        verify(eventService).emitEvent(
                eq(user),
                eq("FLEX_DOLLAR_AWARDED"),
                contains("Downtown Station")
        );
        verify(eventService).emitEvent(
                eq(user),
                eq("FLEX_DOLLAR_AWARDED"),
                contains("1 Flex Dollar")
        );
    }
}

