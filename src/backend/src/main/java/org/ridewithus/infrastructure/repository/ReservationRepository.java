package org.ridewithus.infrastructure.repository;

import org.ridewithus.domain.entity.Reservation;
import org.ridewithus.domain.entity.User;
import org.ridewithus.domain.entity.Reservation.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    Optional<Reservation> findByReservationId(Long reservationId);

    List<Reservation> findByUserId(Long userId);

    List<Reservation> findByBikeIdAndUserId(Long bikeId, Long userId);

    List<Reservation> findByExpiryDateTimeBefore(LocalDateTime now);

    boolean existsByUserAndStatusAndExpiryDateTimeAfter(User user, ReservationStatus status, LocalDateTime expiryDateTime);
}