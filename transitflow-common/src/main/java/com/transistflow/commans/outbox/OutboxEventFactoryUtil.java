package com.transistflow.commans.outbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.transistflow.commans.enmus.OutboxStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OutboxEventFactoryUtil {

    private final ObjectMapper objectMapper;

    public OutboxEvent fromEvent(Object eventPayload, String aggregateId, String aggregateType) {
        return OutboxEvent.builder()
                .id(UUID.randomUUID())
                .aggregateId(aggregateId)
                .aggregateType(aggregateType)
                .eventType(eventPayload.getClass().getSimpleName())
                .payload(serialize(eventPayload))
                .status(OutboxStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private String serialize(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize event", e);
        }
    }


}
