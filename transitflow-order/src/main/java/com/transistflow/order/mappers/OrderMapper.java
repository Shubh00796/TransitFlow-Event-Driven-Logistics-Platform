package com.transistflow.order.mappers;

import com.transistflow.commans.dtos.order.OrderRequestDto;
import com.transistflow.commans.dtos.order.OrderResponseDto;
import com.transistflow.commans.dtos.order.OrderUpdateRequestDto;
import com.transistflow.commans.events.OrderCreatedEvent;
import com.transistflow.commans.events.OrderStatusChangedEvent;
import com.transistflow.order.domain.OrderEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.time.Instant;

@Mapper(componentModel = "spring")
public interface OrderMapper {


    OrderEntity toEntity(OrderRequestDto dto);

    OrderResponseDto toResponse(OrderEntity order);

    OrderCreatedEvent toCreatedEvent(OrderEntity order);

    default OrderStatusChangedEvent toStatusChangedEvent(Long orderId,
                                                         String oldStatus,
                                                         String newStatus,
                                                         Instant occurredAt) {
        OrderStatusChangedEvent event = new OrderStatusChangedEvent();
        event.setOrderId(orderId);
        event.setOldStatus(oldStatus);
        event.setNewStatus(newStatus);
        event.setOccurredAt(occurredAt);
        return event;
    }
    void updateEntityFromRequest(@MappingTarget OrderEntity order, OrderUpdateRequestDto dto);


}
