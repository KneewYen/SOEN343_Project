package org.ridewithus.infrastructure.repository;

import org.ridewithus.domain.entity.Station;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.ridewithus.domain.entity.Bike;
import java.util.List;

@Repository
public interface BikeRepository extends JpaRepository<Bike, Long> {

    List<Bike> findByDock_StationAndStatusIn(Station station, List<String> statuses);


    
}
