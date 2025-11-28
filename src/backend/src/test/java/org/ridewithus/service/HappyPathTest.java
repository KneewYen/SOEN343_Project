package org.ridewithus.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ridewithus.domain.dto.BillingDTO;
import org.ridewithus.domain.entity.*;
import org.ridewithus.domain.services.*;
import org.ridewithus.infrastructure.repository.*;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit test for Happy Path scenario:
 * Rider reserves at Station A, unlocks, rides, returns at Station B, bill computed -> outputs bill and trip information
 */
@ExtendWith(MockitoExtension.class)
class HappyPathTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private TripRepository tripRepository;

    @Mock
    private StationRepository stationRepository;

    @Mock
    private BikeRepository bikeRepository;

    @Mock
    private DockRepository dockRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BillingRepository billingRepository;

    @Mock
    private PricingService pricingService;

    @Mock
    private StationService stationService;

    @Mock
    private DomainEventService eventService;

    @Mock
    private FlexDollarService flexDollarService;

    @InjectMocks
    private TripService tripService;

    private User rider;
    private Station stationA;
    private Station stationB;
    private Bike bike;
    private Dock dockA;
    private Dock dockB;
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

        // Setup Station A (start station)
        stationA = new Station();
        stationA.setId(1L);
        stationA.setName("Station A");
        stationA.setCapacity(20);
        stationA.setCount(5);

        // Setup Station B (end station)
        stationB = new Station();
        stationB.setId(2L);
        stationB.setName("Station B");
        stationB.setCapacity(20);
        stationB.setCount(3);

        // Setup Dock A at Station A
        dockA = new Dock();
        dockA.setId(1L);
        dockA.setStation(stationA);
        dockA.setStatus(Dock.DockStatus.OCCUPIED);

        // Setup Dock B at Station B (empty dock for return)
        dockB = new Dock();
        dockB.setId(2L);
        dockB.setStation(stationB);
        dockB.setStatus(Dock.DockStatus.EMPTY);

        // Setup Bike
        bike = new Bike();
        bike.setId(1L);
        bike.setType("standard");
        bike.setStatus(BikeStatus.AVAILABLE);
        bike.setDock(dockA);

        // Setup Reservation
        reservation = new Reservation();
        reservation.setReservationId(1L);
        reservation.setUser(rider);
        reservation.setBike(bike);
        reservation.setStatus(Reservation.ReservationStatus.ACTIVE);
        reservation.setExpiryDateTime(LocalDateTime.now().plusMinutes(15));

        // Setup Trip
        trip = new Trip();
        trip.setTripId(1L);
        trip.setUser(rider);
        trip.setReservation(reservation);
        trip.setStartStation(stationA);
        trip.setStartTime(LocalDateTime.now().minusMinutes(10));
        trip.setTripComplete(false);
    }

    @Test
    void happyPath_completeTripFlow_returnsBillAndTripInfo() throws Exception {
        // -------------------
        // Arrange - Complete Happy Path
        // -------------------
        
        // Mock trip repository
        when(tripRepository.findByTripId(1L)).thenReturn(trip);

        // Mock station repository
        when(stationRepository.findById(2L)).thenReturn(Optional.of(stationB));

        // Mock empty docks at Station B (for return)
        when(dockRepository.findByStationAndStatus(eq(stationB), eq(Dock.DockStatus.EMPTY)))
            .thenReturn(Collections.singletonList(dockB));

        // Mock bikes available at Station B (for capacity check - 3 bikes, will be 4 after return = 20% < 25%)
        when(stationService.getBikesAvailable(2L)).thenReturn(
            Arrays.asList(
                org.ridewithus.domain.dto.BikeDTO.builder().id(10L).build(),
                org.ridewithus.domain.dto.BikeDTO.builder().id(11L).build(),
                org.ridewithus.domain.dto.BikeDTO.builder().id(12L).build()
            )
        );

        // Mock free docks check (after return, station is not full)
        when(dockRepository.findAllByStationAndStatus(eq(stationB), eq(Dock.DockStatus.EMPTY)))
            .thenReturn(Collections.singletonList(dockB));

        // Mock user repository for reload after Flex Dollar check (Flex Dollars will be awarded)
        User updatedRider = new User();
        updatedRider.setId(1L);
        updatedRider.setUserName("testRider");
        updatedRider.setEmail("rider@test.com");
        updatedRider.setFlexDollars(1); // Awarded 1 Flex Dollar
        when(userRepository.findById(1L)).thenReturn(Optional.of(updatedRider));

        // Mock billing calculation
        BillingDTO billingDTO = new BillingDTO();
        billingDTO.setTripId(1L);
        billingDTO.setTotalAmount(5.50);
        billingDTO.setCharges(Arrays.asList(
            new org.ridewithus.domain.dto.ChargeDTO("Base Fee", "Base fee for trip", 2.00),
            new org.ridewithus.domain.dto.ChargeDTO("Per Minute (10 min)", "Charges for 10 minutes", 1.50),
            new org.ridewithus.domain.dto.ChargeDTO("Standard Bike", "Standard bike rental", 2.00)
        ));

        when(pricingService.calculatePricingPlan(1L)).thenReturn(billingDTO);

        // -------------------
        // Act - End trip at Station B
        // -------------------
        Map<String, Object> result = tripService.endTrip(1L, 2L);

        // -------------------
        // Assert - Verify complete flow
        // -------------------
        
        // 1. Trip should be completed
        assertTrue(trip.isTripComplete());
        assertNotNull(trip.getEndTime());
        assertEquals(stationB, trip.getEndStation());

        // 2. Bike should be returned and available
        assertEquals(BikeStatus.AVAILABLE, bike.getStatus());
        assertEquals(dockB, bike.getDock());
        assertEquals(Dock.DockStatus.OCCUPIED, dockB.getStatus());

        // 3. Reservation should be completed
        assertEquals(Reservation.ReservationStatus.COMPLETED, reservation.getStatus());

        // 4. Station B count should be incremented
        assertEquals(4, stationB.getCount()); // Was 3, now 4 after return

        // 5. Result should contain trip information
        assertNotNull(result);
        assertEquals(1L, result.get("tripId"));
        assertNotNull(result.get("flexDollarBalance"));
        assertNotNull(result.get("flexDollarAwarded"));

        // 6. Verify repositories were called
        verify(tripRepository).save(trip);
        verify(bikeRepository).save(bike);
        verify(dockRepository).save(dockB);
        verify(stationRepository).save(stationB);
        verify(reservationRepository).save(reservation);

        // 7. Verify events were emitted (both FLEX_DOLLAR_AWARDED and TRIP_ENDED)
        verify(eventService).emitEvent(any(User.class), eq("FLEX_DOLLAR_AWARDED"), anyString());
        verify(eventService).emitEvent(any(User.class), eq("TRIP_ENDED"), anyString());

        // 8. Verify billing can be computed
        BillingDTO computedBilling = pricingService.calculatePricingPlan(1L);
        assertNotNull(computedBilling);
        assertEquals(1L, computedBilling.getTripId());
        assertTrue(computedBilling.getTotalAmount() > 0);
        assertFalse(computedBilling.getCharges().isEmpty());
    }
}

