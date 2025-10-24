package org.ridewithus.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripDTO {
    private Long tripId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private boolean tripComplete;
    private Long startStationId;
    private Long endStationId;
    private Long reservationId;
}
