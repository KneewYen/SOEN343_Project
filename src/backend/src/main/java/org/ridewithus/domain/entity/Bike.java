package org.ridewithus.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Data
@Getter
@Setter
@Entity
@Table(name = "bikes")
@NoArgsConstructor
@AllArgsConstructor
public class Bike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type;

    @Enumerated(EnumType.STRING)
    private BikeStatus status; // Persisted in DB

    @Transient
    private BikeState state; // In-memory state

    @ManyToOne
    @JoinColumn(name = "dock_id", unique = true)
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
