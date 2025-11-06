package org.ridewithus.domain.services;

import lombok.RequiredArgsConstructor;
import org.ridewithus.domain.entity.Event;
import org.ridewithus.infrastructure.repository.EventRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.awt.print.Pageable;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    public List<Event> getRecentEvents() {
        return eventRepository.findRecentEvents(PageRequest.of(0, 3));
    }
}
