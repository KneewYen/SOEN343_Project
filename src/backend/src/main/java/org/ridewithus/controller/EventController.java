package org.ridewithus.controller;

import lombok.RequiredArgsConstructor;
import org.ridewithus.domain.dto.EventDTO;
import org.ridewithus.domain.entity.Event;
import org.ridewithus.domain.services.EventService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @GetMapping("/recent")
    public ResponseEntity<?> getRecentEvents() {
        List<EventDTO> dtos = eventService.getRecentEvents()
                .stream()
                .map(EventDTO::fromEntity)
                .toList();

        return ResponseEntity.ok(Map.of("success", true, "events", dtos));
    }
}
