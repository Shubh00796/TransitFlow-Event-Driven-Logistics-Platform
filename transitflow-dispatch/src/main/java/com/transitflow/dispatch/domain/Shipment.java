package com.transitflow.dispatch.domain;


import com.transistflow.commans.enmus.ShipmentStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "shipments",
        indexes = {
                @Index(name = "idx_shipment_order",    columnList = "order_id"),
                @Index(name = "idx_shipment_vehicle",  columnList = "vehicle_id"),
                @Index(name = "idx_shipment_status",   columnList = "status")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Shipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // raw FK to orders.id
    @Column(name = "order_id", nullable = false)
    private Long orderId;

    // raw FK to vehicles.id
    @Column(name = "vehicle_id", nullable = false)
    private Long vehicleId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ShipmentStatus status;

    @CreationTimestamp
    @Column(name = "dispatched_at", nullable = false, updatable = false)
    private Instant dispatchedAt;

    @Column(name = "delivered_at")
    private Instant deliveredAt;
}
