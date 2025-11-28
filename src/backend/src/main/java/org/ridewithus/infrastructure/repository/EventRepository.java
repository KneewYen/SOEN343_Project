package org.ridewithus.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.ridewithus.domain.entity.Event;
import org.ridewithus.domain.entity.User;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    @Query("SELECT e FROM Event e ORDER BY e.timestamp DESC")
    List<Event> findRecentEvents(Pageable pageable);

    boolean existsByUserAndEventTypeAndTimestampAfter(User user, String eventType, LocalDateTime after);

    boolean existsByUserIdAndEventType(User user, String eventType);

}
