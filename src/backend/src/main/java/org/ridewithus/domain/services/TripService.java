package org.ridewithus.domain.services;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.ridewithus.domain.entity.*;
import org.ridewithus.infrastructure.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.ridewithus.domain.dto.TripDTO;

@Service
@NoArgsConstructor
@AllArgsConstructor
public class TripService {

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private DockRepository dockRepository;

    @Autowired
    private BikeRepository bikeRepository;

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private DomainEventService eventService;

    @Transactional
    public Long startTrip(Long reservationId) throws Exception {
        Reservation reservation = reservationRepository.findByReservationId(reservationId);

        if (reservation == null || reservation.getExpiryDateTime().isBefore(LocalDateTime.now())){
            throw new Exception("Reservation is Invalid or has Expired");
        }

        // Check if user is an operator - operators cannot start trips
        if ("operator".equals(reservation.getUser().getRole())) {
            throw new Exception("Operators cannot start trips. Only riders can use bikes.");
        }

        //Finds the station where the bike currently sits
        Station station = reservation.getBike().getDock().getStation();

        if (station == null) {
            throw new Exception("Station does not Exists");
        }
        //Decrements available bikes at that station and marks the dock as empty
        station.setCount(station.getCount() - 1);

        reservation.getBike().getDock().setStatus(Dock.DockStatus.EMPTY);

        dockRepository.save(reservation.getBike().getDock());

        String oldStatus = reservation.getBike().getStatus().toString();

        reservation.getBike().checkOut();

        String newStatus = reservation.getBike().getStatus().toString();

        bikeRepository.save(reservation.getBike());

        stationRepository.save(station);

        Trip trip = Trip.builder()
                .startStation(station)
                .reservation(reservation)
                .user(reservation.getUser())
                .bike(reservation.getBike())
                .startTime(LocalDateTime.now())
                .build();

        tripRepository.save(trip);

        eventService.emitEvent(trip.getReservation().getUser(),"TRIP_STARTED", String.format("Trip %d started - Bike %d: %s -> %s", trip.getTripId(), reservation.getBike().getId(), oldStatus, newStatus));

        return trip.getTripId();

    }

    @Transactional
    public Long endTrip(Long tripId, Long endStationId) throws Exception {

        Trip trip = tripRepository.findByTripId(tripId);

        if (trip == null) {
            throw new Exception("Trip does not Exists");
        }

        Optional<Station> station = stationRepository.findById(endStationId);

        if (station.isEmpty()) {
            throw new Exception("Station does not Exists");
        }

        List<Dock> docks = dockRepository.findByStationAndStatus(station.get(), Dock.DockStatus.EMPTY);

        if (docks.isEmpty()) {
            throw new Exception("No empty places available to Dock");
        }

        //increment available bikes at station
        //Marks one more bike as present at the station and assigns the bike to an empty dock.
        station.get().setCount(station.get().getCount() + 1);

        trip.getReservation().getBike().setDock(docks.getFirst());

        String oldStatus = trip.getReservation().getBike().getStatus().toString();

        trip.getReservation().getBike().returnBike();

        dockRepository.save(trip.getReservation().getBike().getDock());

        bikeRepository.save(trip.getReservation().getBike());

        stationRepository.save(station.get());

        trip.setEndTime(LocalDateTime.now());
        trip.setEndStation(station.get());
        trip.setTripComplete(true);

        //Store user for event before deleting reservation
        User user = trip.getReservation().getUser();

        // Store reservation reference before clearing it
        Reservation reservation = trip.getReservation();

        // Clear the reservation reference from the trip to avoid cascade delete
        trip.setReservation(null);

        tripRepository.save(trip);

        // Clean up the reservation when trip ends (as per BikeShare requirements)
        // Reservations should not remain for record-keeping
        reservationRepository.delete(reservation);

        String newStatus = reservation.getBike().getStatus().toString();

        eventService.emitEvent(user,"TRIP_ENDED", String.format("Trip %d ended - Bike %d: %s -> %s", trip.getTripId(), reservation.getBike().getId(), oldStatus, newStatus));

        return trip.getTripId();

    }

    public List<TripDTO> getUserTrips(Long userId) {
        List<Trip> trips = tripRepository.findByUserId(userId);
        return trips.stream()
                .map(this::mapToDTO)
                .toList();
    }

    public Page<TripDTO> getAllTrips(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("endTime").descending());
        Page<Trip> trips = tripRepository.findAll(pageable);

        return trips.map(this::mapToDTO);
    }

    public Page<TripDTO> getUserTrips(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("endTime").descending());
        Page<Trip> trips = tripRepository.findByUserId(userId, pageable);

        return trips.map(this::mapToDTO);
    }

    private TripDTO mapToDTO(Trip trip) {
        return TripDTO.builder()
                .tripId(trip.getTripId())
                .startTime(trip.getStartTime())
                .endTime(trip.getEndTime())
                .tripComplete(trip.isTripComplete())
                .bikeType(trip.getBike().getType())
                .userName(trip.getUser().getUserName())
                .startStationName(trip.getStartStation() != null ? trip.getStartStation().getName() : null)
                .endStationName(trip.getEndStation() != null ? trip.getEndStation().getName() : null)
                .startStationId(trip.getStartStation() != null ? trip.getStartStation().getId() : null)
                .endStationId(trip.getEndStation() != null ? trip.getEndStation().getId() : null)
                .reservationId(trip.getReservation() != null ? trip.getReservation().getReservationId() : null)
                .build();
    }

}
