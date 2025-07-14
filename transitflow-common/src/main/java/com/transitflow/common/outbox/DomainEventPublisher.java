package com.transitflow.common.outbox;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class DomainEventPublisher {

    private final OutboxEventFactoryUtil factoryUtil;
    private final OutboxEventRepository outboxRepo;

    @Transactional
    public void publish(Object eventPayload, String aggregateId, String aggregateType) {
        OutboxEvent outboxEvent = factoryUtil.fromEvent(eventPayload, aggregateId, aggregateType);
        outboxRepo.save(outboxEvent);
    }
}
