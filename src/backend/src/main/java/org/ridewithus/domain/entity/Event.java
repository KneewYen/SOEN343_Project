package org.ridewithus.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = true)
    private User user;

    private String eventType;
    private String description;
    private LocalDateTime timestamp = LocalDateTime.now();

    public Event(User user, String eventType, String description) {
        this.user = user;
        this.eventType = eventType;
        this.description = description;
    }


}
