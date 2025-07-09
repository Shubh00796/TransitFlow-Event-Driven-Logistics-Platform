package com.transistflow.order.mappers;

import com.transistflow.commans.dtos.order.OrderItemDto;
import com.transistflow.commans.events.OrderItemPayload;
import com.transistflow.order.domain.OrderItemEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {

    OrderItemEntity toEntity(OrderItemDto dto);

    OrderItemDto toDto(OrderItemEntity entity);

    OrderItemPayload toPayload(OrderItemEntity entity);
}
