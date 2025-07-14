package com.transitflow.dispatch.domain;



import com.transistflow.commans.enmus.VehicleStatus;
import com.transistflow.commans.enmus.VehicleType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;


import java.time.Instant;

@Entity
@Table(name = "vehicles",
        indexes = {
                @Index(name = "idx_vehicle_type",      columnList = "type"),
                @Index(name = "idx_vehicle_status",    columnList = "status")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 100)
    private VehicleType type;

    @Column(nullable = false)
    private Integer capacity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private VehicleStatus status;

    @Column(name = "current_location", nullable = false, length = 255)
    private String currentLocation;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
