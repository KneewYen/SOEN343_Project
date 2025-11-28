package org.ridewithus.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@Setter
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "reservation")
public class Reservation {

    public enum ReservationStatus {
        ACTIVE,
        EXPIRED,
        COMPLETED,
        CANCELLED
    }


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id")
    private Long reservationId;

    @ManyToOne
    @JoinColumn(name = "bike_id", unique = true)
    private Bike bike;

    @ManyToOne
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    @Column(name = "expiry_date_time")
    private LocalDateTime expiryDateTime; //Time till user can no longer use this reservation

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;


}
