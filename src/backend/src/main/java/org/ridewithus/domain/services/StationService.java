package org.ridewithus.domain.services;


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

    public int getBikesAvailable(long stationId) throws Exception {

        Optional<Station> station = stationRepository.findById(stationId);

        if (station.isEmpty()) {
            throw new Exception("Station does not exist");
        }

        List<Dock> availableOccupiedDocks = dockRepository.findByStationAndStatus(station.get(), Dock.DockStatus.OCCUPIED);

        return availableOccupiedDocks.size();

    }

    public int getFreeDocks(long stationId) throws Exception {

        Optional<Station> station = stationRepository.findById(stationId);

        if (station.isEmpty()) {
            throw new Exception("Station does not exist");
        }

        List<Dock> availableEmptyDocks = dockRepository.findByStationAndStatus(station.get(), Dock.DockStatus.EMPTY);

        return availableEmptyDocks.size();

    }

}
