package com.transistflow.Inventory.consumers;


import com.transistflow.Inventory.services.InventoryService;
import com.transitflow.common.events.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderCreatedEventConsumer {

    private final InventoryService inventoryService;

    /**
     * Listens for OrderCreatedEvent messages on the "ordercreatedevent" topic,
     * then delegates to the inventory service to reserve stock.
     */
    @KafkaListener(
            topics = "ordercreatedevent",
            groupId = "inventory-group",
            containerFactory = "orderCreatedEventKafkaListenerContainerFactory"
    )
    public void onOrderCreated(@Payload OrderCreatedEvent event) {
        log.info("📥 Received OrderCreatedEvent: orderId={} items={}",
                event.getOrderId(), event.getItems().size());

        try {
            inventoryService.processOrderCreatedEvent(event);
        } catch (Exception ex) {
            // Log and let the OutboxPublisher / retry mechanism handle retries
            log.error("❌ Error processing OrderCreatedEvent for orderId={}: {}",
                    event.getOrderId(), ex.getMessage(), ex);
            throw ex; // rethrow to trigger retry/backoff if configured
        }
    }
}
