package org.ridewithus.domain.dto;

import lombok.*;
import org.ridewithus.domain.entity.Bike;
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

    public static BikeDTO fromEntity(Bike b) {
        return BikeDTO.builder()
                .id(b.getId())
                .type(b.getType())
                .status(b.getStatus())
                .dockId(b.getDock() != null ? b.getDock().getId() : null)
                .build();
    }


}


