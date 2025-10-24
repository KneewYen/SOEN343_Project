package org.ridewithus.domain.dto;

import lombok.*;
import org.ridewithus.domain.entity.BikeStatus;
import org.ridewithus.domain.entity.Dock;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DockDTO {

    private Long id;

    private Dock.DockStatus status;

    private Long stationId;

    private Long bikeId;

    private BikeStatus bikeStatus;

}
