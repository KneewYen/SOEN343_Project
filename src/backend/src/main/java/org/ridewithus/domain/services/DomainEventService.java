package org.ridewithus.domain.services;

import org.ridewithus.domain.entity.Event;
import org.ridewithus.infrastructure.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class DomainEventService {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private EventRepository eventRepository;

    public void emitEvent(String type, String description) {
        // Persist to DB
        Event event = new Event(type, description);
        eventRepository.save(event);

        // Publish to event system
        eventPublisher.publishEvent(event);
    }
}
