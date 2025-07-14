package com.transitflow.order.domain;

import com.transistflow.commans.enmus.OrderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(
        name = "orders_for_transistflow ",
        indexes = {
                @Index(name = "idx_orders_customer_id", columnList = "customerId"),
                @Index(name = "idx_orders_status", columnList = "status")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Raw FK to customers table—no JPA relationship for loose coupling. */
    @Column(nullable = false)
    private Long customerId;

    @Column(nullable = false, length = 255)
    private String origin;

    @Column(nullable = false, length = 255)
    private String destination;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private OrderStatus status;  // e.g. "ORDERED", "CANCELLED"

    /**
     * DB will fill this on INSERT.
     * Prevent Hibernate from including it in the SQL.
     */
    @Column(
            nullable = false,
            updatable = false,
            insertable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
    )
    private Instant createdAt;

    /**
     * DB will fill/refresh this on INSERT and UPDATE.
     * Prevent Hibernate from including it in the INSERT.
     */
    @Column(
            nullable = false,
            insertable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP"
    )
    private Instant updatedAt;
}
