package org.ridewithus.domain.services;

import org.ridewithus.domain.entity.Bike;
import org.ridewithus.domain.entity.BikeStatus;
import org.ridewithus.domain.entity.Reservation;
import org.ridewithus.infrastructure.repository.BikeRepository;
import org.ridewithus.infrastructure.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReservationExpiryService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private BikeRepository bikeRepository;

    @Autowired
    DomainEventService eventService;

    @Scheduled (fixedRate = 60000) //run every 60 seconds
    public void deleteExpiredReservations() {

        LocalDateTime now = LocalDateTime.now();

        List<Reservation> expired = reservationRepository.findByExpiryDateTimeBefore(now);

        for (Reservation reservation : expired) {
            Bike bike = reservation.getBike();

            String oldStatus = bike.getStatus().toString();

            bike.setStatus(BikeStatus.AVAILABLE);
            bikeRepository.save(bike);

            String newStatus = bike.getStatus().toString();

            eventService.emitEvent("RESERVATION_EXPIRED", String.format("Reservation %d expired - Bike %d: %s -> %s", reservation.getReservationId(), bike.getId(), oldStatus, newStatus));

            reservationRepository.delete(reservation);
        }
    }
}
