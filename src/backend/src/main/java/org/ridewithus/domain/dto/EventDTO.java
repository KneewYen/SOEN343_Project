package org.ridewithus.domain.dto;

import lombok.*;
import org.ridewithus.domain.entity.Event;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventDTO {
    private String eventType;
    private String description;
    private LocalDateTime timestamp;
    private UserDTO user;

    public static EventDTO fromEntity(Event e) {
        return EventDTO.builder()
                .eventType(e.getEventType())
                .description(e.getDescription())
                .timestamp(e.getTimestamp())
                .user(e.getUser() != null ? UserDTO.fromEntity(e.getUser()) : null)
                .build();
    }
}
