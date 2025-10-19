package org.ridewithus.domain.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationDTO {
    private Long reservationId;
    private UserDTO user;
    private BikeDTO bike;
    private LocalDateTime expiryDateTime;
}
