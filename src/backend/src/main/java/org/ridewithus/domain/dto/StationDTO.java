package org.ridewithus.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Getter;
import org.ridewithus.domain.entity.Station;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StationDTO {

    private Long id;

    private int count;

    private String name;

    private int capacity;

    private String address;

    private Double latitude;

    private Double longitude;

    private List<DockDTO> dockIds;

    private Station.StationStatus status;
}
