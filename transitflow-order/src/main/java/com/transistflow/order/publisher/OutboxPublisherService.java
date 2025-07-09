package com.transistflow.order.publisher;

import com.transistflow.commans.enmus.OutboxStatus;
import com.transistflow.order.domain.OutboxEvent;
import com.transistflow.order.reposiotries.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OutboxPublisherService {

    private final OutboxEventRepository outboxRepo;
    private final KafkaTemplate<String, String> kafkaTemplate;

    /**
     * Every 3 seconds, pick up to 100 PENDING events (oldest first),
     * publish them, and update their status in the same transaction.
     */
    @Scheduled(fixedDelay = 3000)
    @Transactional
    public void publishPendingEvents() {
        List<OutboxEvent> events = outboxRepo
                .findTop100ByStatusOrderByCreatedAtAsc(OutboxStatus.PENDING);

        for (OutboxEvent event : events) {
            UUID eventId = event.getId();
            try {
                // 1) Publish to Kafka (topic = eventType or a fixed topic)
                kafkaTemplate.send(
                        event.getEventType().toLowerCase(),   // e.g. "ordercreated"
                        event.getAggregateId(),               // key
                        event.getPayload()                    // JSON payload
                ).get(); // block if you want sync guarantee

                // 2) Mark as PUBLISHED
                outboxRepo.updateStatusById(
                        eventId,
                        OutboxStatus.PUBLISHED,
                        LocalDateTime.now()
                );

                log.info("Published OutboxEvent {} → Kafka topic '{}'",
                        eventId, event.getEventType());

            } catch (Exception ex) {
                log.error("Failed to publish OutboxEvent {}: {}", eventId, ex.getMessage());
                // 3) Mark as FAILED so you can alert or retry later
                outboxRepo.updateStatusById(
                        eventId,
                        OutboxStatus.FAILED,
                        LocalDateTime.now()
                );
            }
        }
    }
}
