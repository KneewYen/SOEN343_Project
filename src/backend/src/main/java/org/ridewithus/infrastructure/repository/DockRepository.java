package org.ridewithus.infrastructure.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.ridewithus.domain.entity.Dock;
import org.ridewithus.domain.entity.Station;
import org.ridewithus.domain.entity.Bike;

import java.util.List;
import java.util.Optional;

@Repository
public interface DockRepository extends JpaRepository<Dock, Long> {

    // returns number of bikes in a station by searching a certain station and dock status (occupied or empty)
    long countByStationAndStatus(Station station, String status);

    // returns the list of bikes docked, similar to the one above
    List<Bike> findByStationAndStatus(Station station, String status);

    Optional<Dock> findFirstByStationAndStatus(Station station, Dock.DockStatus status);

    
}
