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
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OutboxPublisherService {

    private final OutboxEventRepository outboxRepo;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Scheduled(fixedDelay = 3000)
    @Transactional
    public void publishPendingEvents() {
        outboxRepo
                .findTop100ByStatusOrderByCreatedAtAsc(OutboxStatus.PENDING)
                .forEach(this::handleEvent);
    }

    private void handleEvent(OutboxEvent event) {
        UUID eventId = event.getId();
        try {
            publishToKafka(event);
            updateEventStatus(eventId, OutboxStatus.PUBLISHED);
            logSuccess(event);
        } catch (Exception ex) {
            updateEventStatus(eventId, OutboxStatus.FAILED);
            logFailure(eventId, ex);
        }
    }

    private void publishToKafka(OutboxEvent event) throws Exception {
        kafkaTemplate.send(
                event.getEventType().toLowerCase(), // topic
                event.getAggregateId(),             // key
                event.getPayload()                  // payload
        ).get(); // block for sync delivery
    }

    private void updateEventStatus(UUID eventId, OutboxStatus status) {
        outboxRepo.updateStatusById(eventId, status, LocalDateTime.now());
    }

    private void logSuccess(OutboxEvent event) {
        log.info("Published OutboxEvent {} → Kafka topic '{}'",
                event.getId(), event.getEventType());
    }

    private void logFailure(UUID eventId, Exception ex) {
        log.error("Failed to publish OutboxEvent {}: {}", eventId, ex.getMessage());
    }
}
