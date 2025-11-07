package org.ridewithus.infrastructure.repository;

import org.ridewithus.domain.entity.Charge;
import org.ridewithus.domain.entity.Billing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChargeRepository extends JpaRepository<Charge, Long> {
    List<Charge> findByBilling(Billing billing);
    List<Charge> findByBilling_BillingId(Long billingId);
}

