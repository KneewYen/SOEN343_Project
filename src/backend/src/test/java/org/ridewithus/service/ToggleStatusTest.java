package org.ridewithus.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.ridewithus.domain.entity.*;
import org.ridewithus.domain.services.DomainEventService;
import org.ridewithus.domain.services.OperatorService;
import org.ridewithus.infrastructure.repository.BikeRepository;
import org.ridewithus.infrastructure.repository.DockRepository;
import org.ridewithus.infrastructure.repository.StationRepository;
import static org.mockito.Mockito.lenient;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

//@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
public class ToggleStatusTest {

    @Mock
    private BikeRepository bikeRepository;
    @Mock
    private DockRepository dockRepository;
    @Mock
    private StationRepository stationRepository;
    @Mock
    private DomainEventService eventService;

    @InjectMocks
    private OperatorService operatorService;

    private User operator;

    @BeforeEach
    void setup() {
        operator = new User();
        operator.setId(99L);
        operator.setRole("operator");

        reset(bikeRepository, dockRepository, stationRepository, eventService);
    }

    //toggleBikeStatus tests

    @Test
    void toggleBikeStatus_success() {
        Bike bike = new Bike();
        bike.setId(1L);
        bike.setStatus(BikeStatus.AVAILABLE);

        lenient().when(bikeRepository.findById(any(Long.class))).thenReturn(Optional.of(bike));

        String result = operatorService.toggleBikeStatus(1L, operator);

        assertEquals("Bike status updated successfully", result);
        assertEquals(BikeStatus.MAINTENANCE, bike.getStatus());

        verify(bikeRepository).save(bike);
        verify(eventService).emitEvent(
                eq(operator),
                eq("BIKE_MAINTENANCE"),
                contains("Bike 1: AVAILABLE -> MAINTENANCE")
        );
    }

    @Test
    void toggleBikeStatus_unauthorized() {
        User user = new User();
        user.setRole("rider");

        String result = operatorService.toggleBikeStatus(1L, user);

        assertEquals("Error: Unauthorized", result);
        verifyNoInteractions(bikeRepository, eventService);
    }

    @Test
    void toggleBikeStatus_disallowedState_reservedOrOnTrip() {
        Bike bike = new Bike();
        bike.setId(2L);
        bike.setStatus(BikeStatus.RESERVED);

        lenient().when(bikeRepository.findById(any(Long.class))).thenReturn(Optional.of(bike));

        String result = operatorService.toggleBikeStatus(2L, operator);

        assertEquals("Error: Bike cannot be toggled while reserved or on a trip.", result);
        verifyNoInteractions(eventService);
    }

    //toggleDockStatus Tests

    @Test
    void toggleDockStatus_success() {
        Dock dock = spy(new Dock());
        dock.setId(10L);
        dock.setStatus(Dock.DockStatus.EMPTY);

        lenient().when(dockRepository.findById(any(Long.class))).thenReturn(Optional.of(dock));
        when(dock.hasActiveReservation()).thenReturn(false);

        String result = operatorService.toggleDockStatus(10L, operator);

        assertEquals("Dock status updated successfully", result);
        assertEquals(Dock.DockStatus.OUT_OF_SERVICE, dock.getStatus());

        verify(dockRepository).save(dock);
        verify(eventService).emitEvent(
                eq(operator),
                eq("DOCK_MAINTENANCE"),
                contains("Dock 10: EMPTY -> OUT_OF_SERVICE")
        );
    }

    @Test
    void toggleDockStatus_unauthorized() {
        User user = new User();
        user.setRole("rider");

        String result = operatorService.toggleDockStatus(10L, user);

        assertEquals("Error: Unauthorized", result);
        verifyNoInteractions(dockRepository, eventService);
    }

    @Test
    void toggleDockStatus_hasActiveReservation() {
        Dock dock = mock(Dock.class);
        lenient().when(dockRepository.findById(any(Long.class))).thenReturn(Optional.of(dock));
        when(dock.hasActiveReservation()).thenReturn(true);

        String result = operatorService.toggleDockStatus(10L, operator);

        assertEquals("Error: Dock cannot be toggled while it has active reservations.", result);
        verifyNoInteractions(eventService);
    }

    //toggleStationStatus Tests

    @Test
    void toggleStationStatus_success() {
        Station realStation = new Station();
        realStation.setId(5L);
        realStation.setName("McGill");
        realStation.setStatus(Station.StationStatus.ACTIVE);
        realStation.setDocks(new ArrayList<>());

        Station stationSpy = spy(realStation);

        lenient().when(stationRepository.findById(any(Long.class))).thenReturn(Optional.of(stationSpy));
        doReturn(false).when(stationSpy).hasActiveReservation();

        String result = operatorService.toggleStationStatus(5L, operator);

        assertEquals("Station status updated successfully", result);
        assertEquals(Station.StationStatus.OUT_OF_SERVICE, stationSpy.getStatus());

        verify(stationRepository).save(stationSpy);
        verify(eventService).emitEvent(
                eq(operator),
                eq("STATION_MAINTENANCE"),
                contains("Station 5 (McGill): ACTIVE -> OUT_OF_SERVICE")
        );
    }

    @Test
    void toggleStationStatus_unauthorized() {
        User user = new User();
        user.setRole("rider");

        String result = operatorService.toggleStationStatus(5L, user);

        assertEquals("Error: Unauthorized", result);
        verifyNoInteractions(stationRepository, eventService);
    }

    @Test
    void toggleStationStatus_hasActiveReservation() {
        Station station = mock(Station.class);
        lenient().when(stationRepository.findById(5L)).thenReturn(Optional.of(station));
        when(station.hasActiveReservation()).thenReturn(true);

        String result = operatorService.toggleStationStatus(5L, operator);

        assertEquals("Error: Station cannot be toggled while it has active reservations.", result);
        verifyNoInteractions(eventService);
    }

}
