package com.transistflow.order.domain;


import com.transistflow.commans.enmus.OrderStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "orders",
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

    /**
     * Raw FK to customers table—no JPA relationship for loose coupling.
     */
    @Column(nullable = false)
    private Long customerId;

    @Column(nullable = false, length = 255)
    private String origin;

    @Column(nullable = false, length = 255)
    private String destination;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private OrderStatus status;  // e.g. "ORDERED", "CANCELLED"

    /**
     * DB defaults CURRENT_TIMESTAMP / ON UPDATE CURRENT_TIMESTAMP
     * We let the database populate these.
     */
    @Column(nullable = false, updatable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private java.time.Instant createdAt;

    @Column(nullable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private java.time.Instant updatedAt;
}
