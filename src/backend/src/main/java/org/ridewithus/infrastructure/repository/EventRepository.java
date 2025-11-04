package org.ridewithus.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.ridewithus.domain.entity.Event;

public interface EventRepository extends JpaRepository<Event, Long> {
}
