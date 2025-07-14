package com.transitflow.order.mappers;

import com.transitflow.common.dtos.order.OrderItemDto;
import com.transitflow.common.dtos.order.OrderRequestDto;
import com.transitflow.common.dtos.order.OrderResponseDto;
import com.transitflow.common.dtos.order.OrderUpdateRequestDto;
import com.transitflow.common.events.OrderCreatedEvent;
import com.transitflow.common.events.OrderItemPayload;
import com.transitflow.order.domain.OrderEntity;
import org.mapstruct.*;

import java.util.List;
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OrderMapper {

    OrderEntity toEntity(OrderRequestDto dto);

    @Mapping(target = "items", ignore = true)
    OrderResponseDto toResponse(OrderEntity order);

    // ✅ REMOVE @Context
    @Mappings({
            @Mapping(source = "id", target = "orderId"),
            @Mapping(source = "customerId", target = "customerId"),
            @Mapping(source = "origin", target = "origin"),
            @Mapping(source = "destination", target = "destination"),
            @Mapping(source = "createdAt", target = "createdAt"),
            @Mapping(target = "warehouseId", ignore = true)
    })
    OrderCreatedEvent toCreatedEventBase(OrderEntity order);

    // ✅ Use default method to manually inject items
    default OrderCreatedEvent toCreatedEvent(OrderEntity order, List<OrderItemPayload> items) {
        OrderCreatedEvent event = toCreatedEventBase(order);
        event.setItems(items);
        return event;
    }

    @Mapping(target = "price", source = "price")
    OrderItemPayload toPayload(OrderItemDto dto);

    void updateEntityFromRequest(@MappingTarget OrderEntity order, OrderUpdateRequestDto dto);
}
