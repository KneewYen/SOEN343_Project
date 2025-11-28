package org.ridewithus.domain.services;

import jakarta.transaction.Transactional;
import org.h2.schema.Domain;
import org.ridewithus.domain.dto.BikeDTO;
import org.ridewithus.domain.dto.DockDTO;
import org.ridewithus.domain.dto.StationDTO;
import org.ridewithus.domain.entity.*;
import org.ridewithus.infrastructure.repository.DockRepository;
import org.ridewithus.infrastructure.repository.StationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class StationService {

    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private DockRepository dockRepository;
    @Autowired
    private DomainEventService eventService;

    @Transactional
    public List<StationDTO> getAllStations() {
        List<Station> stations = stationRepository.findAll();

        return stations.stream().map(station ->
                StationDTO.builder()
                        .id(station.getId())
                        .latitude(station.getLatitude())
                        .longitude(station.getLongitude())
                        .address(station.getAddress())
                        .name(station.getName())
                        .count(station.getCount())
                        .status(station.getStatus())
                        .capacity(station.getCapacity())
                        .dockIds(station.getDocks().stream().map(this::mapToDockDTO).toList())
                        .build()).toList();
    }

    public List<BikeDTO> getBikesAvailable(long stationId) throws Exception {

        Optional<Station> station = stationRepository.findById(stationId);

        if (station.isEmpty()) {
            throw new Exception("Station does not exist");
        }

        List<Dock> docks = dockRepository.findByStationAndStatus(station.get(), Dock.DockStatus.OCCUPIED);

        // Filter out null bikes and only include bikes with AVAILABLE status
        List<Bike> bikes = docks.stream()
                .map(Dock::getBike)
                .filter(Objects::nonNull).filter(bike -> bike != null && bike.getStatus() == BikeStatus.AVAILABLE)
                .toList();

        return bikes.stream().map(bike ->
                BikeDTO.builder()
                        .id(bike.getId())
                        .type(bike.getType())
                        .status(bike.getStatus())
                        .rating(bike.getRating())
                        .dockId(bike.getDock().getId())
                        .build()).toList();

    }

    public List<DockDTO> getFreeDocks(long stationId) throws Exception {

        Optional<Station> station = stationRepository.findById(stationId);

        if (station.isEmpty()) {
            throw new Exception("Station does not exist");
        }

        List<Dock> docks = dockRepository.findByStationAndStatus(station.get(), Dock.DockStatus.EMPTY);

        return docks.stream().map(this::mapToDockDTO).toList();
    }

    public void checkRebalance(User user, Long stationId) {
        Station station = stationRepository.findById(stationId)
                .orElseThrow(() -> new RuntimeException("Station not found"));

        long bikes = station.getDocks().stream()
                .filter(d -> d.getBike() != null)
                .count();

        if (bikes == 0) {
            eventService.emitEvent(user,"ALERT_OPERATOR", station.getName() + " station has no bikes. Rebalance required");
        }
    }

    private DockDTO mapToDockDTO(Dock dock) {
        return DockDTO.builder()
                        .id(dock.getId())
                        .status(dock.getStatus())
                        .stationId(dock.getStation().getId())
                        .bike(dock.getBike() != null ? BikeDTO.builder()
                                .id(dock.getBike().getId() != null ? dock.getBike().getId() : null)
                                .type(dock.getBike().getType() != null ? dock.getBike().getType() : null)
                                .status(dock.getBike().getStatus() != null ? dock.getBike().getStatus() : null)
                                .build() : null)
                        .bikeStatus(dock.getBike() != null ? dock.getBike().getStatus() : null)
                        .build();
    }

}
