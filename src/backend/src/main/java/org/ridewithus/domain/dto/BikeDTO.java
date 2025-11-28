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
    private Long rating;
    private Long numReviews;

    public static BikeDTO fromEntity(Bike b) {
        return BikeDTO.builder()
                .id(b.getId())
                .type(b.getType())
                .status(b.getStatus())
                .rating(b.getRating())
                .dockId(b.getDock() != null ? b.getDock().getId() : null)
                .build();
    }
}


