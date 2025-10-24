package org.ridewithus.domain.services;

import jakarta.transaction.Transactional;
import org.ridewithus.domain.dto.BikeDTO;
import org.ridewithus.domain.dto.DockDTO;
import org.ridewithus.domain.dto.StationDTO;
import org.ridewithus.domain.entity.Bike;
import org.ridewithus.domain.entity.BikeStatus;
import org.ridewithus.domain.entity.Dock;
import org.ridewithus.domain.entity.Station;
import org.ridewithus.infrastructure.repository.DockRepository;
import org.ridewithus.infrastructure.repository.StationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StationService {

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private DockRepository dockRepository;

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

        List<Bike> bikes = docks.stream().map(Dock::getBike).filter(bike -> bike.getStatus() == BikeStatus.AVAILABLE).toList();

        return bikes.stream().map(bike ->
                BikeDTO.builder()
                        .id(bike.getId())
                        .type(bike.getType())
                        .status(bike.getStatus())
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

    private DockDTO mapToDockDTO(Dock dock) {
        return DockDTO.builder()
                        .id(dock.getId())
                        .status(dock.getStatus())
                        .stationId(dock.getStation().getId())
                        .bikeId(dock.getBike() != null ? dock.getBike().getId() : null)
                        .bikeStatus(dock.getBike() != null ? dock.getBike().getStatus() : null)
                        .build();
    }

}
