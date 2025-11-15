package org.ridewithus.infrastructure.repository;

import org.ridewithus.domain.entity.PricingPlan;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface PricingPlanRepository extends JpaRepository<PricingPlan, Long> {
    
}
