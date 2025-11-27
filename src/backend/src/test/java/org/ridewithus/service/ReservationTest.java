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
public class ReservationTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private BikeRepository bikeRepository;
    @Mock
    private ReservationRepository reservationRepository;
    @Mock
    private TripRepository tripRepository;

    @InjectMocks
    private ReservationService reservationService;

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

        stationA = Station.builder().id(100L).name("Station A").count(5).status(Station.StationStatus.ACTIVE).build();
        stationB = Station.builder().id(200L).name("Station B").count(2).status(Station.StationStatus.ACTIVE).build();

        dockA = Mockito.spy(Dock.builder().id(500L).station(stationA)
                .status(Dock.DockStatus.OCCUPIED).build());
        dockB = Mockito.spy(Dock.builder().id(600L).station(stationB)
                .status(Dock.DockStatus.EMPTY).build());

        bike = Mockito.spy(Bike.builder()
                .id(1L)
                .type("standard")
                .status(BikeStatus.AVAILABLE)
                .build());

        bike.initState();

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
        Mockito.doNothing().when(bike).reserve();
        Mockito.doReturn(BikeStatus.RESERVED).when(bike).getStatus();

        when(userRepository.findById(10L)).thenReturn(Optional.of(rider));
        when(reservationRepository.findByUserId(10L)).thenReturn(List.of());
        when(bikeRepository.findById(eq(1L))).thenReturn(Optional.of(bike));
        when(reservationRepository.save(any())).thenReturn(reservation);
        when(bikeRepository.save(any())).thenReturn(bike);

        Long reservationId = reservationService.createReservation(1L, 10L);
        assertEquals(300L, reservationId);

        verify(bike).reserve(); // Ensure the status method was called
        assertEquals(BikeStatus.RESERVED, bike.getStatus(), "Bike status should be RESERVED after reservation.");
    }
}