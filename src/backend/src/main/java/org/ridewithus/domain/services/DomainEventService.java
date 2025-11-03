package org.ridewithus.domain.services;

import org.ridewithus.domain.entity.Event;
import org.ridewithus.domain.entity.User;
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

    // Emit event with no user (system event)
    public void emitEvent(String eventType, String description) {
        Event event = Event.builder()
                .user(null)
                .eventType(eventType)
                .description(description)
                .build();
        eventRepository.save(event);
        // Publish to event system
        eventPublisher.publishEvent(event);

    }

    // Emit event tied to a user
    public void emitEvent(User user, String eventType, String description) {
        Event event = Event.builder()
                .user(user)
                .eventType(eventType)
                .description(description)
                .build();
        eventRepository.save(event);

        // Publish to event system
        eventPublisher.publishEvent(event);
    }


}
