package org.ridewithus.infrastructure.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.ridewithus.domain.entity.Station;

@Repository
public interface StationRepository extends JpaRepository<Station, Long> {



    
}
