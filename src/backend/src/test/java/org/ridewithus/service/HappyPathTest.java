package org.ridewithus.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.ridewithus.domain.dto.BillingDTO;
import org.ridewithus.domain.entity.*;
import org.ridewithus.domain.services.*;
import org.ridewithus.infrastructure.repository.*;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
public class HappyPathTest {

    @Mock private UserRepository userRepository;
    @Mock private BikeRepository bikeRepository;
    @Mock private ReservationRepository reservationRepository;
    @Mock private TripRepository tripRepository;
    @Mock private DockRepository dockRepository;
    @Mock private StationRepository stationRepository;
    @Mock private PricingPlanRepository pricingPlanRepository;
    @Mock private BillingRepository billingRepository;
    @Mock private ChargeRepository chargeRepository;
    @Mock private DomainEventService eventService;
    @Mock private StationService stationService;

    @InjectMocks
    private ReservationService reservationService;

    @InjectMocks
    private TripService tripService;

    @InjectMocks
    private PricingService pricingService;

    private User rider;
    private Bike bike;
    private Station stationA;
    private Station stationB;
    private Dock dockA;
    private Dock dockB;
    private Reservation reservation;
    private Trip trip;
    private PricingPlan plan;


    @BeforeEach
    void setup() {
        // --- Test Data ---
        rider = User.builder()
                .id(10L)
                .role("rider")
                .pricingPlan(PricingPlan.builder().pricingPlanId(99L).name("Standard plan").build())
                .build();

        plan = rider.getPricingPlan();

        bike = spy(Bike.builder()
                .id(1L)
                .type("standard")
                .status(BikeStatus.AVAILABLE)
                .build());

        stationA = Station.builder().id(100L).name("Station A").count(5).build();
        stationB = Station.builder().id(200L).name("Station B").count(2).build();

        dockA = spy(Dock.builder().id(500L).station(stationA)
                .status(Dock.DockStatus.OCCUPIED).build());
        dockB = spy(Dock.builder().id(600L).station(stationB)
                .status(Dock.DockStatus.EMPTY).build());

        bike.setDock(dockA);

        reservation = Reservation.builder()
                .reservationId(300L)
                .bike(bike)
                .user(rider)
                .expiryDateTime(LocalDateTime.now().plusMinutes(20))
                .status(Reservation.ReservationStatus.ACTIVE)
                .build();

        trip = Trip.builder()
                .tripId(400L)
                .reservation(reservation)
                .user(rider)
                .bike(bike)
                .startStation(stationA)
                .startTime(LocalDateTime.now())
                .build();
    }

    @Test
    void completeRide_happyPath_success() throws Exception {

        // ----------- RESERVATION CREATION -------------
        when(userRepository.findById(10L)).thenReturn(Optional.of(rider));
        when(reservationRepository.findByUserId(10L)).thenReturn(List.of());
        when(tripRepository.findByReservationUserId(10L)).thenReturn(List.of());
        when(bikeRepository.findById(eq(1L))).thenReturn(Optional.of(bike));
        when(reservationRepository.save(any())).thenReturn(reservation);
        when(bikeRepository.save(any())).thenReturn(bike);

        Long reservationId = reservationService.createReservation(1L, 10L);
        assertEquals(300L, reservationId);

        verify(bike).reserve(); // Ensure the status method was called
        assertEquals(BikeStatus.RESERVED, bike.getStatus(), "Bike status should be RESERVED after reservation.");

        // ----------- START TRIP -------------
        when(reservationRepository.findByReservationId(300L))
                .thenReturn(Optional.of(reservation));

        when(dockRepository.save(any())).thenReturn(dockA);
        when(stationRepository.save(any())).thenReturn(stationA);
        when(tripRepository.save(any())).thenReturn(trip);

        Long tripId = tripService.startTrip(300L);
        assertEquals(400L, tripId);

        verify(bike).checkOut(); // Ensure the status method was called
        assertEquals(BikeStatus.ON_TRIP, bike.getStatus(), "Bike status should be ON_TRIP after starting trip.");
        verify(dockA).setStatus(Dock.DockStatus.EMPTY);

        // ----------- END TRIP -------------
        when(tripRepository.findByTripId(400L)).thenReturn(trip);
        when(stationRepository.findById(200L)).thenReturn(Optional.of(stationB));
        when(dockRepository.findByStationAndStatus(stationB, Dock.DockStatus.EMPTY))
                .thenReturn(List.of(dockB));

        when(bikeRepository.save(any())).thenReturn(bike);
        when(dockRepository.save(any())).thenReturn(dockB);
        when(tripRepository.save(any())).thenReturn(trip);
        when(reservationRepository.save(any())).thenReturn(reservation);


        trip.setStartTime(LocalDateTime.now().minusMinutes(10));

        Long endedTripId = tripService.endTrip(400L, 200L);
        assertEquals(400L, endedTripId);

        verify(bike).returnBike(); // Ensure bike status was reverted
        assertEquals(BikeStatus.AVAILABLE, bike.getStatus(), "Bike status should be AVAILABLE after trip ends.");
        verify(dockB).setStatus(Dock.DockStatus.OCCUPIED);

        // ----------- PRICING -------------
        when(tripRepository.findByTripId(400L)).thenReturn(trip);
        when(billingRepository.findByTrip(trip)).thenReturn(Optional.empty());
        when(billingRepository.save(any()))
                .thenAnswer(inv -> {
                    Billing b = inv.getArgument(0);
                    b.setBillingId(700L);
                    return b;
                });
        when(chargeRepository.saveAll(any()))
                .thenAnswer(inv -> inv.getArgument(0));

        BillingDTO bill = pricingService.calculatePricingPlan(400L);

        assertEquals(700L, bill.getBillingId());
        assertEquals(400L, bill.getTripId());
        assertTrue(bill.getTotalAmount() > 0);

        // ----------- VERIFY EVENTS WERE EMITTED -------------
        verify(eventService, atLeastOnce()).emitEvent(eq(rider), contains("RESERVATION_CREATED"), anyString());
        verify(eventService, atLeastOnce()).emitEvent(eq(rider), contains("TRIP_STARTED"), anyString());
        verify(eventService, atLeastOnce()).emitEvent(eq(rider), contains("TRIP_ENDED"), anyString());

        // ----------- FINAL ASSERTIONS -------------
        assertTrue(trip.isTripComplete());
        assertEquals(Reservation.ReservationStatus.COMPLETED, reservation.getStatus());

        verify(stationService, times(1)).checkRebalance(eq(rider), eq(stationA.getId()));
    }
}