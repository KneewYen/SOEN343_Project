package org.ridewithus.domain.services;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.ridewithus.domain.entity.*;
import org.ridewithus.infrastructure.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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

    @Transactional
    public Long startTrip(Long reservationId) throws Exception {
        Reservation reservation = reservationRepository.findByReservationId(reservationId);

        if (reservation == null || reservation.getExpiryDateTime().isBefore(LocalDateTime.now())){
            throw new Exception("Reservation is Invalid or has Expired");
        }

        Station station = reservation.getBike().getDock().getStation();

        if (station == null) {
            throw new Exception("Station does not Exists");
        }

        station.setCount(station.getCount() - 1);

        reservation.getBike().getDock().setStatus(Dock.DockStatus.EMPTY);

        dockRepository.save(reservation.getBike().getDock());

        reservation.getBike().checkOut();

        bikeRepository.save(reservation.getBike());

        stationRepository.save(station);

        Trip trip = Trip.builder().startStation(station).reservation(reservation).startTime(LocalDateTime.now()).build();

        tripRepository.save(trip);

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

        station.get().setCount(station.get().getCount() + 1);

        trip.getReservation().getBike().setDock(docks.getFirst());

        trip.getReservation().getBike().returnBike();

        dockRepository.save(trip.getReservation().getBike().getDock());

        bikeRepository.save(trip.getReservation().getBike());

        stationRepository.save(station.get());

        trip.setEndTime(LocalDateTime.now());
        trip.setEndStation(station.get());
        trip.setTripComplete(true);

        tripRepository.save(trip);

        return trip.getTripId();

    }

}
