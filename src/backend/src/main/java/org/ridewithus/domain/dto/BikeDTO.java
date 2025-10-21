package org.ridewithus.domain.dto;

import lombok.*;
import org.ridewithus.domain.entity.BikeStatus;

@Data
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BikeDTO {
    private Long id;
    private String type;
    private BikeStatus status;
    private Long dockId;
}
