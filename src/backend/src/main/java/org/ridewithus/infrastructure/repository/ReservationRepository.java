package org.ridewithus.infrastructure.repository;

import org.ridewithus.domain.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    Reservation findByReservationId(Long reservationId);

    List<Reservation> findByUserId(Long userId);

    List<Reservation> findByBikeIdAndUserId(Long bikeId, Long userId);

    List<Reservation> findByExpiryDateTimeBefore(LocalDateTime now);

    List<Reservation> findByUserIdAndStatus(Long userId, Reservation.ReservationStatus status);

}