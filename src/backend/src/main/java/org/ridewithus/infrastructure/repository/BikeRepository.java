package org.ridewithus.infrastructure.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.ridewithus.domain.entity.Bike;

@Repository
public interface BikeRepository extends JpaRepository<Bike, Long> {



    
}
