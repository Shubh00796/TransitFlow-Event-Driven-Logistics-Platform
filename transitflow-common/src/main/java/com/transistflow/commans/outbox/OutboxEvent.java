package com.transistflow.commans.outbox;

import com.transistflow.commans.enmus.OutboxStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;


@Entity
@Table(name = "outbox_event", indexes = {
        @Index(name = "idx_outbox_status", columnList = "status"),
        @Index(name = "idx_outbox_created_at", columnList = "createdAt"),
        @Index(name = "idx_outbox_status_created_at", columnList = "status, createdAt")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OutboxEvent {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String aggregateId;          // e.g., Order ID

    @Column(nullable = false)
    private String aggregateType;        // e.g., "Order"

    @Column(nullable = false)
    private String eventType;            // e.g., "OrderCreated"

    @Lob
    @Column(nullable = false)
    private String payload;              // Serialized JSON or Avro

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OutboxStatus status;
    // "pending", "published", "failed", etc.

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
