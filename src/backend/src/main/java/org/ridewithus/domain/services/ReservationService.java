package org.ridewithus.domain.services;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.ridewithus.domain.dto.BikeDTO;
import org.ridewithus.domain.dto.ReservationDTO;
import org.ridewithus.domain.dto.UserDTO;
import org.ridewithus.domain.entity.Bike;
import org.ridewithus.domain.entity.Reservation;
import org.ridewithus.domain.entity.Trip;
import org.ridewithus.domain.entity.User;
import org.ridewithus.infrastructure.repository.BikeRepository;
import org.ridewithus.infrastructure.repository.ReservationRepository;
import org.ridewithus.infrastructure.repository.TripRepository;
import org.ridewithus.infrastructure.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@NoArgsConstructor
@AllArgsConstructor
public class ReservationService {

    @Autowired
    private BikeRepository bikeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    public ReservationDTO getReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findByReservationId(reservationId);
        return mapToDTO(reservation);
    }

    public Long createReservation(Long bikeId, Long userId) throws Exception {

        Optional<User> user = userRepository.findById(userId);

        if (user.isEmpty()) {
            throw new Exception("User not found");
        }

        // Check if user is an operator - operators cannot reserve bikes
        if ("operator".equals(user.get().getRole())) {
            throw new Exception("Operators cannot reserve bikes. Only riders can make reservations.");
        }

        List<Reservation> reservations = reservationRepository.findByUserId(userId);

        boolean hasActiveReservation = reservations.stream().anyMatch(res -> res.getExpiryDateTime().isAfter(LocalDateTime.now()));

        if (hasActiveReservation) {
            throw new Exception("You already have an active reservation");
        }

        List<Trip> userTrips = tripRepository.findByReservationUserId(userId);

        boolean hasActiveTrip = userTrips.stream().anyMatch(trip -> !trip.isTripComplete());

        if (hasActiveTrip) {
            throw new Exception("You already have an active trip");
        }

        Optional<Bike> bike = bikeRepository.findById(bikeId);

        if (bike.isEmpty()) {
            throw new Exception("Bike not found");
        }

        bike.get().reserve();

        bikeRepository.save(bike.get());

        Reservation reservation = Reservation.builder().bike(bike.get()).user(user.get()).build();

        reservationRepository.save(reservation);

        return reservation.getReservationId();

    }


    public boolean isReservationValid(Long reservationId) {
        Reservation reservation = reservationRepository.findByReservationId(reservationId);

        return reservation != null && reservation.getExpiryDateTime().isAfter(LocalDateTime.now());
    }

    public void deleteReservation(Long reservationId) throws Exception {
        Reservation reservation = reservationRepository.findByReservationId(reservationId);

        if (reservation == null) {
            throw new Exception("Reservation not found");
        }

        reservation.getBike().returnBike();

        bikeRepository.save(reservation.getBike());

        reservationRepository.deleteById(reservationId);
    }

    public List<ReservationDTO> getUserReservations(Long userId) {
        List<Reservation> reservations = reservationRepository.findByUserId(userId);
        return reservations.stream()
                .filter(res -> res.getExpiryDateTime().isAfter(LocalDateTime.now()))
                .map(this::mapToDTO)
                .toList();
    }

    private ReservationDTO mapToDTO(Reservation reservation) {
        User user = reservation.getUser();

        Bike bike = reservation.getBike();

        UserDTO userDTO = UserDTO.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .userName(user.getUserName())
                .address(user.getAddress())
                .email(user.getEmail())
                .role(user.getRole())
                .build();

        BikeDTO bikeDTO = BikeDTO.builder()
                .id(bike.getId())
                .type(bike.getType())
                .status(bike.getStatus())
                .dockId(bike.getDock() != null ? bike.getDock().getId() : null)
                .build();

        return ReservationDTO.builder()
                .reservationId(reservation.getReservationId())
                .user(userDTO)
                .bike(bikeDTO)
                .expiryDateTime(reservation.getExpiryDateTime()).build();
    }

}
