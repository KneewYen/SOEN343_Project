package org.ridewithus.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String eventType;
    private String description;
    private LocalDateTime timestamp = LocalDateTime.now();

    public Event() {}

    public Event(String eventType, String description) {
        this.eventType = eventType;
        this.description = description;
    }

}
