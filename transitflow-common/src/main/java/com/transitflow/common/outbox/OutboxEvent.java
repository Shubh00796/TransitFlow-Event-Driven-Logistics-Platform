package com.transitflow.common.outbox;

import com.transitflow.common.enmus.OutboxStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;


@Entity
@Table(name = "outbox_event")
@Getter
@Setter
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
