package org.ridewithus.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Data
@Getter
@Setter
@Entity
@Builder
@Table(name = "bikes")
@NoArgsConstructor
@AllArgsConstructor
public class Bike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type;

    @Convert(converter = BikeStatusConverter.class)
    private BikeStatus status; // Persisted in DB

    @Transient
    @JsonIgnore
    private BikeState state; // In-memory state

    @OneToOne
    @JoinColumn(name = "dock_id", unique = true) //unique makes it so it has acts as One-to-one relationship, more flexible for later on
    private Dock dock;

    // Methods delegate to state
    public void reserve() throws Exception {
        if (state != null) {
            state.reserve(this);
        }
    }

    public void checkOut() throws Exception {
        if (state != null) {
            state.checkOut(this);
        }
    }

    public void returnBike() throws Exception {
        if (state != null) {
            state.returnBike(this);
        }
    }

    public void changeState(BikeState newState) {
        this.state = newState;
    }

    // Initialize state based on status after loading from DB
    @PostLoad
    public void initState() {
        if (status == null) return;

        switch (status) {
            case AVAILABLE -> this.state = new AvailableState();
            case RESERVED -> this.state = new ReservedState();
            case ON_TRIP -> this.state = new OnTripState();
            case MAINTENANCE -> this.state = new MaintenanceState();
        }
    }
}
