package org.ridewithus.listeners;

import org.ridewithus.domain.entity.Event;
import org.springframework.context.event.EventListener;

import java.util.EventObject;

public class EventLogger {

    @EventListener
    public void handleDomainEvent(Event event) {
        System.out.println("Event: " + event.getEventType() + " - " + event.getDescription());
    }
}
