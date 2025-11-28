/*package org.ridewithus.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.quality.Strictness;
import org.mockito.junit.jupiter.MockitoSettings;

import org.ridewithus.domain.entity.*;
import org.ridewithus.domain.loyaltyProgram.ChainOfR.*;
import org.ridewithus.domain.services.DomainEventService;
import org.ridewithus.domain.services.StationService;
import org.ridewithus.domain.services.TripService;
import org.ridewithus.domain.services.UserService;
import org.ridewithus.infrastructure.repository.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;
import java.util.List;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TripServiceTest {

    private TripService tripService;

    private BronzeHandler bronzeHandler = mock(BronzeHandler.class);
    private SilverHandler silverHandler = mock(SilverHandler.class);
    private GoldHandler goldHandler = mock(GoldHandler.class);

    private UserService userService = mock(UserService.class);
    private TripRepository tripRepository = mock(TripRepository.class);
    private DockRepository dockRepository = mock(DockRepository.class);
    private BikeRepository bikeRepository = mock(BikeRepository.class);
    private StationRepository stationRepository = mock(StationRepository.class);
    private ReservationRepository reservationRepository = mock(ReservationRepository.class);
    private DomainEventService eventService = mock(DomainEventService.class);
    private StationService stationService = mock(StationService.class);

    @BeforeEach
    void setup() {
        tripService = new TripService(
                bronzeHandler,
                silverHandler,
                goldHandler,
                userService,
                tripRepository,
                dockRepository,
                bikeRepository,
                stationRepository,
                reservationRepository,
                eventService,
                stationService
        );

        bronzeHandler.setNext(silverHandler);
        silverHandler.setNext(goldHandler);
    }

    @Test
    void testGetTierByUser_TierChanges() {
        User user = new User();
        user.setLoyaltyTier(Tier.BRONZE);

        // Suppose chain upgrades BRONZE -> SILVER
        when(bronzeHandler.handle(user)).thenReturn(Tier.SILVER);

        Tier result = tripService.getTierByUser(user);

        assertEquals(Tier.SILVER, result);
        verify(userService).save(user);
        assertEquals(Tier.SILVER, user.getPrevLoyaltyTier());
    }

    @Test
    void testGetTierByUser_NoChange() {
        User user = new User();
        user.setLoyaltyTier(Tier.SILVER);

        // Chain returns same tier → no upgrade
        when(bronzeHandler.handle(user)).thenReturn(Tier.SILVER);

        Tier result = tripService.getTierByUser(user);

        assertNull(result);
        verify(userService).save(user);
        assertEquals(Tier.SILVER, user.getPrevLoyaltyTier());
    }

    @Test
    void testEndTripSuccess() throws Exception {
        Long tripId = 10L;
        Long stationId = 20L;

        // ---- Mock Entities ----
        User user = new User();
        user.setUserName("Ria");

        Station endStation = new Station();
        endStation.setId(stationId);
        endStation.setName("Station A");
        endStation.setCount(5);

        Dock emptyDock = new Dock();
        emptyDock.setStatus(Dock.DockStatus.EMPTY);
        emptyDock.setStation(endStation);

        Bike bike = new Bike();
        bike.setId(33L);
        bike.setStatus(BikeStatus.ON_TRIP);

        Reservation reservation = new Reservation();
        reservation.setBike(bike);
        reservation.setUser(user);
        reservation.setStatus(Reservation.ReservationStatus.ACTIVE);

        Trip trip = new Trip();
        trip.setTripId(tripId);
        trip.setReservation(reservation);
        trip.setStartStation(endStation);
        trip.setUser(user);
        trip.setBike(bike);

        when(tripRepository.findByTripId(tripId)).thenReturn(trip);
        when(stationRepository.findById(stationId)).thenReturn(Optional.of(endStation));
        when(dockRepository.findByStationAndStatus(endStation, Dock.DockStatus.EMPTY))
                .thenReturn(List.of(emptyDock));

        Long result = tripService.endTrip(tripId, stationId);

        assertEquals(tripId, result);
        assertNotNull(trip.getEndTime());
        assertEquals(endStation, trip.getEndStation());
        assertTrue(trip.isTripComplete());

        verify(stationRepository).save(endStation);
        verify(tripRepository).save(trip);
        verify(reservationRepository).save(reservation);

        verify(eventService).emitEvent(
                eq(user),
                eq("TRIP_ENDED"),
                anyString()
        );
    }
}
*/