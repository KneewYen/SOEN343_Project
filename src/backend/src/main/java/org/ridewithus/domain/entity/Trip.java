package org.ridewithus.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Data
@Builder
@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "trip")
public class Trip {

    @Id
    @Column(name = "trip_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tripId;

    @OneToOne
    @JoinColumn(name = "reservation_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Reservation reservation;

    @ManyToOne
    @JoinColumn(name = "start_station_id")
    private Station startStation;

    @ManyToOne
    @JoinColumn(name = "end_station_id")
    private Station endStation;

    @Builder.Default
    @Column(name = "trip_complete")
    private boolean tripComplete = false;

    @Builder.Default
    @Column(name = "start_time")
    private LocalDateTime startTime = LocalDateTime.now();

    @Column(name = "end_time")
    private LocalDateTime endTime;
    
    @Column(name = "user_id")
    private Long userId;

}
