package org.ridewithus.domain.services;

import jakarta.transaction.Transactional;
import org.ridewithus.domain.entity.*;
import org.ridewithus.infrastructure.repository.BikeRepository;
import org.ridewithus.infrastructure.repository.DockRepository;
import org.ridewithus.infrastructure.repository.StationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.ridewithus.domain.entity.BikeStatus.ON_TRIP;
import static org.ridewithus.domain.entity.BikeStatus.RESERVED;

@Service
public class OperatorService {

    @Autowired
    private BikeRepository bikeRepository;
    @Autowired
    private DockRepository dockRepository;
    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private DomainEventService eventService;

    @Transactional
    public String toggleBikeStatus(Long bikeId, User operator){
        if(!operator.getRole().equals("operator")){
            return "Error: Unauthorized";
        }
        Bike bike = bikeRepository.findById(bikeId).orElseThrow(() -> new RuntimeException("Bike not found"));

        if (bike.getStatus() == RESERVED || bike.getStatus() == ON_TRIP) {
            return "Error: Bike cannot be toggled while reserved or on a trip.";
        }

        String oldStatus = bike.getStatus().toString();

        bike.setStatus(bike.getStatus() == BikeStatus.AVAILABLE ? BikeStatus.MAINTENANCE : BikeStatus.AVAILABLE);
        // Update the bike's state to match the new status
        bike.initState();
        bikeRepository.save(bike);

        String newStatus = bike.getStatus().toString();

        eventService.emitEvent(operator,"BIKE_MAINTENANCE", String.format("Bike %d: %s -> %s", bike.getId(), oldStatus, newStatus));

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

        String oldStatus = dock.getStatus().toString();


        switch (dock.getStatus()) {

            case OCCUPIED:
                dock.setStatus(Dock.DockStatus.OUT_OF_SERVICE);
                break;

            case OUT_OF_SERVICE:
                // if dock has a bike, go back to OCCUPIED
                if (dock.getBike() != null) {
                    dock.setStatus(Dock.DockStatus.OCCUPIED);
                } else {
                    // no bike, make it EMPTY
                    dock.setStatus(Dock.DockStatus.EMPTY);
                }
                break;

            case EMPTY:
                dock.setStatus(Dock.DockStatus.OUT_OF_SERVICE);
                break;
        }

        dockRepository.save(dock);

        String newStatus = dock.getStatus().toString();
        eventService.emitEvent(operator,"DOCK_MAINTENANCE", String.format("Dock %d: %s -> %s", dock.getId(), oldStatus, newStatus));

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

        String oldStatus = station.getStatus().toString();

        station.setStatus(station.getStatus() == Station.StationStatus.ACTIVE ? Station.StationStatus.OUT_OF_SERVICE : Station.StationStatus.ACTIVE);
        stationRepository.save(station);

        String newStatus = station.getStatus().toString();
        eventService.emitEvent(operator,"STATION_MAINTENANCE", String.format("Station %d (%s): %s -> %s", station.getId(), station.getName(), oldStatus, newStatus));

        return "Station status updated successfully";
    }

    // Move single bike
    @Transactional
    public String moveBike(Long bikeId, Long sourceStationId, Long destinationStationId, User operator){
        if(!operator.getRole().equals("operator")){
            return "Error: Unauthorized";
        }

        Bike bike = bikeRepository.findById(bikeId).orElseThrow(() -> new RuntimeException("Bike not found"));

        Station sourceStation = stationRepository.findById(sourceStationId).orElseThrow(() -> new RuntimeException("Station not found"));

        Station destinationStation = stationRepository.findById(destinationStationId).orElseThrow(() -> new RuntimeException("Station not found"));

        if(sourceStation.getStatus() != Station.StationStatus.ACTIVE){
            return "Source station is not active";
        }

        if(destinationStation.getStatus() != Station.StationStatus.ACTIVE){
            return "Destination station is not active";
        }

        if(bike.getStatus() == ON_TRIP || bike.getStatus() == RESERVED){
            return "Bike is currently unavailable for transfer";
        }

        // Find free dock in destination
        Dock freeDock = dockRepository.findFirstByStationAndStatus(destinationStation, Dock.DockStatus.EMPTY)
                .orElseThrow(() -> new RuntimeException("No free docks available in destination station."));

        String oldDock = bike.getDock().getStation().getName();

        // Remove bike from current dock
        Dock currentDock = bike.getDock();
        if(currentDock != null){
            currentDock.setStatus(Dock.DockStatus.EMPTY);
            dockRepository.save(currentDock);
        }

        // Assign to new dock
        bike.setDock(freeDock);
        freeDock.setStatus(Dock.DockStatus.OCCUPIED);

        dockRepository.save(freeDock);
        bikeRepository.save(bike);

        String newDock = bike.getDock().getStation().getName();
        eventService.emitEvent(operator,"REBALANCE", String.format("Bike %d: %s -> %s", bike.getId(), oldDock, newDock));

        // Get free docks at destination after rebalancing
        List<Dock> freeDocksAfterRebalance = dockRepository.findAllByStationAndStatus(destinationStation, Dock.DockStatus.EMPTY);

        if(freeDocksAfterRebalance.isEmpty()){
            eventService.emitEvent(operator, "STATION_FULL", String.format("%s station is full", destinationStation.getName()));
        }

        return "Bike " + bike.getId() + " successfully moved from station " + sourceStation.getName()
                + " to station " + destinationStation.getName();

    }

    // Bulk rebalancing
    @Transactional
    public String rebalanceBikes(Long sourceStationId, Long destinationStationId, int numberOfBikes, User operator){
        if(!operator.getRole().equals("operator")){
            return "Error: Unauthorized";
        }

        Station sourceStation = stationRepository.findById(sourceStationId).orElseThrow(() -> new RuntimeException("Station not found"));

        Station destinationStation = stationRepository.findById(destinationStationId).orElseThrow(() -> new RuntimeException("Station not found"));

        if(sourceStation.getStatus() != Station.StationStatus.ACTIVE){
            return "Source station is not active";
        }

        if(destinationStation.getStatus() != Station.StationStatus.ACTIVE){
            return "Destination station is not active";
        }

        // Get available bikes from source
        List<Bike> availableBikes = bikeRepository.findByDock_StationAndStatusIn(sourceStation, List.of("AVAILABLE", "MAINTENANCE"));

        if(availableBikes.size() < numberOfBikes){
            return "Not enough available bikes in source station.";
        }

        // Get free docks at destination
        List<Dock> freeDocks = dockRepository.findAllByStationAndStatus(destinationStation, Dock.DockStatus.EMPTY);

        if(freeDocks.size() < numberOfBikes){
            return "Not enough free docks in destination station.";
        }

        // Move bikes
        for(int i = 0; i < numberOfBikes; i++){
            Bike bike = availableBikes.get(i);
            Dock oldDock = bike.getDock();
            Dock newDock = freeDocks.get(i);

            String oldDockStr = bike.getDock().getStation().getName();

            // Update old dock
            oldDock.setStatus(Dock.DockStatus.EMPTY);
            dockRepository.save(oldDock);

            // Update new dock and bike
            newDock.setStatus(Dock.DockStatus.OCCUPIED);
            bike.setDock(newDock);

            dockRepository.save(newDock);
            bikeRepository.save(bike);



            String newDockStr = bike.getDock().getStation().getName();
            eventService.emitEvent(operator,"REBALANCE", String.format("Bike %d: %s -> %s", bike.getId(), oldDockStr, newDockStr));
        }

        // Get free docks at destination after rebalancing
        List<Dock> freeDocksAfterRebalance = dockRepository.findAllByStationAndStatus(destinationStation, Dock.DockStatus.EMPTY);

        if(freeDocksAfterRebalance.isEmpty()){
            eventService.emitEvent(operator, "STATION_FULL", String.format("%s station is full", destinationStation.getName()));
        }

        return numberOfBikes + " bikes successfully rebalanced from " + sourceStation.getName() + " to " + destinationStation.getName();


    }
}
