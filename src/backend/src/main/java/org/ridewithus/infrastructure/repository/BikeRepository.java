package org.ridewithus.infrastructure.repository;

import jakarta.transaction.Transactional;
import org.ridewithus.domain.entity.Station;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.ridewithus.domain.entity.Bike;
import java.util.List;

@Repository
public interface BikeRepository extends JpaRepository<Bike, Long> {

    List<Bike> findByDock_StationAndStatusIn(Station station, List<String> statuses);

    Bike findById(long id);

    // Bike findByBikeId(Long bikeId);

    @Modifying
    @Transactional
    @Query("UPDATE Bike b SET b.dock = NULL")
    void clearAllDockAssignments();

}
