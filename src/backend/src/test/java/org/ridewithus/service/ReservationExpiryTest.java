package org.ridewithus.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ridewithus.domain.entity.Bike;
import org.ridewithus.domain.entity.Reservation;
import org.ridewithus.domain.entity.BikeStatus;
import org.ridewithus.domain.services.ReservationExpiryService;
import org.ridewithus.domain.services.DomainEventService;
import org.ridewithus.infrastructure.repository.BikeRepository;
import org.ridewithus.infrastructure.repository.ReservationRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReservationExpiryTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private BikeRepository bikeRepository;

    @Mock
    private DomainEventService eventService;

    @InjectMocks
    private ReservationExpiryService reservationExpiryService;

    @Test
    void deleteExpiredReservations_updatesBikeAndReservation_andEmitsEvent() {
        // -------------------
        // Arrange
        // -------------------
        Bike bike = new Bike();
        bike.setId(10L);
        bike.setStatus(BikeStatus.RESERVED);

        Reservation reservation = new Reservation();
        reservation.setReservationId(5L);
        reservation.setStatus(Reservation.ReservationStatus.ACTIVE);
        reservation.setBike(bike);
        reservation.setExpiryDateTime(LocalDateTime.now().minusMinutes(5)); // expired

        when(reservationRepository.findByExpiryDateTimeBefore(any()))
                .thenReturn(List.of(reservation));

        // -------------------
        // Act
        // -------------------
        reservationExpiryService.deleteExpiredReservations();

        // -------------------
        // Assert
        // -------------------
        // Bike must change to AVAILABLE
        assert(bike.getStatus() == BikeStatus.AVAILABLE);

        // Reservation must change to EXPIRED
        assert(reservation.getStatus() == Reservation.ReservationStatus.EXPIRED);

        // Saves must be called
        verify(bikeRepository).save(bike);
        verify(reservationRepository).save(reservation);

        // Event must be emitted
        verify(eventService).emitEvent(
                eq("RESERVATION_EXPIRED"),
                contains("Reservation 5 expired")
        );
    }
}
