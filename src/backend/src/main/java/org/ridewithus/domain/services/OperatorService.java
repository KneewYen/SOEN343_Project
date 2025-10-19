package org.ridewithus.domain.services;

import jakarta.transaction.Transactional;
import org.ridewithus.domain.entity.*;
import org.ridewithus.infrastructure.repository.BikeRepository;
import org.ridewithus.infrastructure.repository.DockRepository;
import org.ridewithus.infrastructure.repository.StationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class OperatorService {

    @Autowired
    private BikeRepository bikeRepository;
    @Autowired
    private DockRepository dockRepository;
    @Autowired
    private StationRepository stationRepository;

    @Transactional
    public String toggleBikeStatus(Long bikeId, User operator){
        if(!operator.getRole().equals("operator")){
            return "Error: Unauthorized";
        }
        Bike bike = bikeRepository.findById(bikeId).orElseThrow(() -> new RuntimeException("Bike not found"));

        if (bike.getStatus() == BikeStatus.RESERVED || bike.getStatus() == BikeStatus.ON_TRIP) {
            return "Error: Bike cannot be toggled while reserved or on a trip.";
        }

        bike.setStatus(bike.getStatus() == BikeStatus.AVAILABLE ? BikeStatus.MAINTENANCE : BikeStatus.AVAILABLE);
        bikeRepository.save(bike);

        return "Bike status updated successfully";
    }

    @Transactional
    public String toggleDockStatus(Long dockId, User operator){
        if(!operator.getRole().equals("operator")){
            return "Error: Unauthorized";
        }

        Dock dock = dockRepository.findById(dockId).orElseThrow(() -> new RuntimeException("Dock not found"));

        if(dock.hasActiveReservation()){
            return "Error: Dock cannot be toggled while it has active reservations.";
        }

        dock.setStatus(dock.getStatus() == Dock.DockStatus.EMPTY ? Dock.DockStatus.OUT_OF_SERVICE : Dock.DockStatus.EMPTY);
        dockRepository.save(dock);

        return "Dock status updated successfully";

    }

    @Transactional
    public String toggleStationStatus(Long stationId, User operator){
        if(!operator.getRole().equals("operator")){
            return "Error: Unauthorized";
        }

        Station station = stationRepository.findById(stationId).orElseThrow(() -> new RuntimeException("Station not found"));

        if(station.hasActiveReservation()) {
            return "Error: Station cannot be toggled while it has active reservations.";
        }

        station.setStatus(station.getStatus() == Station.StationStatus.ACTIVE ? Station.StationStatus.OUT_OF_SERVICE : Station.StationStatus.ACTIVE);
        stationRepository.save(station);

        return "Station status updated successfully";
    }
}
