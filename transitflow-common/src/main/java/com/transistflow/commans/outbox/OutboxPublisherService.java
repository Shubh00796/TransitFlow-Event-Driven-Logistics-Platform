package com.transistflow.commans.outbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.transistflow.commans.enmus.OutboxStatus;
import com.transistflow.commans.events.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
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
        String topic = event.getEventType().toLowerCase();
        String key = event.getAggregateId();
        String payload = event.getPayload();

        Object payloadObj = resolvePayload(topic, payload); // Deserialize

        kafkaTemplate.send(topic, key, payloadObj).get();
    }
    private Object resolvePayload(String topic, String json) throws Exception {
        if (topic.equals("ordercreatedevent") || topic.equals("order")) {
            return new ObjectMapper().readValue(json, OrderCreatedEvent.class);
        }
        throw new IllegalArgumentException("Unsupported topic: " + topic);
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
