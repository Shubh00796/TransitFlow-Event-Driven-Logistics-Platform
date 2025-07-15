package com.transitflow.delivery.domain;


import com.transitflow.common.enmus.ShipmentStatus;
import com.transitflow.delivery.utils.MetadataUtils;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.Map;

@Entity
@Table(name = "shipment_events", indexes = {
        @Index(name = "idx_shipment_id", columnList = "shipment_id"),
        @Index(name = "idx_event_type", columnList = "event_type"),
        @Index(name = "idx_occurred_at", columnList = "occurred_at")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "shipment_id", nullable = false)
    private Long shipmentId;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false, length = 50)
    private ShipmentStatus eventType;

    @CreationTimestamp
    @Column(name = "occurred_at", nullable = false)
    private Instant occurredAt;

    @Column(name = "metadata", columnDefinition = "JSON")
    private String metadata;

    // Helper methods for metadata handling
    public void setMetadataMap(Map<String, Object> metadataMap) {
        this.metadata = MetadataUtils.toJson(metadataMap);
    }

    public Map<String, Object> getMetadataMap() {
        return MetadataUtils.fromJson(metadata);
    }

}