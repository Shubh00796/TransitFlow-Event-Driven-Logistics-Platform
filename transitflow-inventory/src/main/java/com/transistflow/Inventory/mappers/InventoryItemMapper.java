package com.transistflow.Inventory.mappers;


import com.transistflow.Inventory.domain.InventoryItemEntity;
import com.transistflow.commans.dtos.inventory.InventoryItemDto;
import org.mapstruct.*;

/**
 * MapStruct mapper for converting between InventoryItemEntity and InventoryItemDto.
 */
@Mapper(componentModel = "spring") // for Spring injection
public interface InventoryItemMapper {

    InventoryItemDto toDto(InventoryItemEntity entity);

    InventoryItemEntity toEntity(InventoryItemDto dto);

    /**
     * Updates an existing entity from a DTO without replacing it.
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(InventoryItemDto dto, @MappingTarget InventoryItemEntity entity);
}
