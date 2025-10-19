package org.ridewithus.domain.dto;

import lombok.*;
import org.ridewithus.domain.entity.Dock;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DockDTO {
    private Long id;
    private Dock.DockStatus status;
    private StationDTO station;
}
