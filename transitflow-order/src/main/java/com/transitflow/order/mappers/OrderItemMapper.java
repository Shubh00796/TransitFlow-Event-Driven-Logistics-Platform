// src/main/java/com/transistflow/order/mappers/OrderItemMapper.java
package com.transitflow.order.mappers;

import com.transitflow.common.dtos.order.OrderItemDto;
import com.transitflow.order.domain.OrderItemEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface OrderItemMapper {

    @Mapping(source = "productId", target = "productId")
    @Mapping(source = "quantity",  target = "quantity")
    @Mapping(source = "price",     target = "price")
    OrderItemEntity toEntity(OrderItemDto dto);

    @Mapping(source = "productId", target = "productId")
    @Mapping(source = "quantity",  target = "quantity")
    @Mapping(source = "price",     target = "price")
    OrderItemDto toDto(OrderItemEntity entity);
}
