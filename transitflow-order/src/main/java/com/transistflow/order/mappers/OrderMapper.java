package com.transistflow.order.mappers;

import com.transistflow.commans.dtos.order.OrderRequestDto;
import com.transistflow.commans.dtos.order.OrderResponseDto;
import com.transistflow.commans.dtos.order.OrderUpdateRequestDto;
import com.transistflow.commans.events.OrderCreatedEvent;
import com.transistflow.commans.events.OrderStatusChangedEvent;
import com.transistflow.order.domain.OrderEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.time.Instant;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE  // ignore anything we haven’t explicitly mapped
)
public interface OrderMapper {

    /**
     * Map only the fields that actually exist on OrderEntity.
     * (customerId, origin, destination, status, plus timestamps are set in your service.)
     */
    OrderEntity toEntity(OrderRequestDto dto);

    /**
     * Build a response DTO from the entity.
     * We ignore `items` here because OrderEntity has no such field.
     */
    @Mapping(target = "items", ignore = true)
    OrderResponseDto toResponse(OrderEntity order);

    OrderCreatedEvent toCreatedEvent(OrderEntity order);

    void updateEntityFromRequest(
            @MappingTarget OrderEntity order,
            OrderUpdateRequestDto dto
    );

    default OrderStatusChangedEvent toStatusChangedEvent(
            Long   orderId,
            String oldStatus,
            String newStatus,
            Instant occurredAt
    ) {
        OrderStatusChangedEvent event = new OrderStatusChangedEvent();
        event.setOrderId(orderId);
        event.setOldStatus(oldStatus);
        event.setNewStatus(newStatus);
        event.setOccurredAt(occurredAt);
        return event;
    }
}
