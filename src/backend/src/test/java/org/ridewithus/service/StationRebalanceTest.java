package org.ridewithus.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ridewithus.domain.entity.Dock;
import org.ridewithus.domain.entity.Station;
import org.ridewithus.domain.entity.User;
import org.ridewithus.domain.services.DomainEventService;
import org.ridewithus.domain.services.StationService;
import org.ridewithus.infrastructure.repository.StationRepository;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StationRebalanceTest {

    @Mock
    private StationRepository stationRepository;

    @InjectMocks
    private StationService stationService;

    @Mock
    private DomainEventService eventService;

    @Test
    void checkRebalance_emitsAlertEvent_whenStationIsEmpty() {

        User user = new User();

        // Station with zero bikes
        Station station = new Station();
        station.setName("test");
        Dock dock1 = new Dock();
        dock1.setBike(null);

        Dock dock2 = new Dock();
        dock2.setBike(null);

        station.setDocks(List.of(dock1, dock2));

        when(stationRepository.findById(1L)).thenReturn(Optional.of(station));

        // Act
        stationService.checkRebalance(user,1L);

        // Assert
        verify(eventService, times(1)).emitEvent(
                eq(user),
                eq( "ALERT_OPERATOR"),
                contains("test station has no bikes")
        );
    }

}

