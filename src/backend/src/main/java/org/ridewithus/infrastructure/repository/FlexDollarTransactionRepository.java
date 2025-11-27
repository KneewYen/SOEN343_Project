package org.ridewithus.infrastructure.repository;

import org.ridewithus.domain.entity.FlexDollarTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FlexDollarTransactionRepository extends JpaRepository<FlexDollarTransaction, Long> {
    List<FlexDollarTransaction> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    List<FlexDollarTransaction> findByUserId(Long userId);
}

