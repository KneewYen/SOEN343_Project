package org.ridewithus.domain.dto;

import lombok.*;
import org.ridewithus.domain.entity.Reservation;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationDTO {
    private boolean success;
    private Long reservationId;
    private UserDTO user;
    private BikeDTO bike;
    private String station;
    private LocalDateTime expiryDateTime;

    public static ReservationDTO fromEntity(Reservation r) {

        return ReservationDTO.builder()
                .success(true)
                .reservationId(r.getReservationId())
                .bike(BikeDTO.fromEntity(r.getBike()))
                .station(r.getBike().getDock() != null ? r.getBike().getDock().getStation().getName() : null)
                .expiryDateTime(r.getExpiryDateTime())
                .build();
    }

}
