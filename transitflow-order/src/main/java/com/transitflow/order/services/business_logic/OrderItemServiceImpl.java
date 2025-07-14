package com.transitflow.order.services.business_logic;

import com.transistflow.commans.dtos.order.OrderItemDto;
import com.transitflow.order.domain.OrderItemEntity;
import com.transitflow.order.mappers.OrderItemMapper;
import com.transitflow.order.reposiotries.data_access.OrderItemRepoService;
import com.transitflow.order.services.OrderItemService;
import com.transitflow.order.utils.OrderValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderItemServiceImpl implements OrderItemService {

    private final OrderItemRepoService repoService;
    private final OrderItemMapper orderItemMapper;
    private final OrderValidationUtil validationUtil;

    @Override
    public OrderItemDto createOrderItem(Long orderId, OrderItemDto itemDto) {
        log.info("Creating order item for order ID: {}", orderId);
        validationUtil.validateCreateOrderItem(orderId, itemDto);
        OrderItemEntity entity = mapToEntityWithOrderId(itemDto, orderId);
        entity.setOrderId(orderId);
        return saveAndConvert(entity);
    }

    @Override
    public OrderItemDto updateOrderItem(Long orderId, Long itemId, OrderItemDto itemDto) {
        log.info("Updating order item ID: {} for order ID: {}", itemId, orderId);
        validationUtil.validateUpdateOrderItem(orderId, itemId, itemDto);

        OrderItemEntity existing = repoService.findById(itemId);
        OrderItemEntity updated = mapToEntityWithIdAndOrderId(itemDto, existing.getId(), orderId);

        return saveAndConvert(updated);
    }

    @Override
    public void deleteOrderItem(Long orderId, Long itemId) {
        log.info("Deleting order item ID: {} for order ID: {}", itemId, orderId);
        OrderItemEntity item = repoService.findById(itemId);
        validateItemBelongsToOrder(item, orderId);
        repoService.delete(item);
    }

    @Override
    public Page<OrderItemDto> getItemsByOrder(Long orderId, Pageable pageable) {
        log.debug("Fetching paginated items for order ID: {}", orderId);
        return repoService.findByOrderId(orderId, pageable)
                .map(orderItemMapper::toDto);
    }

    @Override
    public List<OrderItemDto> getAllItemsByOrder(Long orderId) {
        log.debug("Fetching all items for order ID: {}", orderId);
        return convertList(repoService.findAllByOrderId(orderId));
    }

    @Override
    public Stream<OrderItemDto> streamItemsByOrder(Long orderId) {
        log.debug("Streaming items for order ID: {}", orderId);
        return repoService.streamByOrderId(orderId)
                .map(orderItemMapper::toDto);
    }




    // ───────────────────────────────
    // Helper Methods (DRY Utilities)
    // ───────────────────────────────

    private OrderItemEntity mapToEntityWithOrderId(OrderItemDto dto, Long orderId) {
        OrderItemEntity entity = orderItemMapper.toEntity(dto);
        entity.setOrderId(orderId);
        return entity;
    }

    private OrderItemEntity mapToEntityWithIdAndOrderId(OrderItemDto dto, Long itemId, Long orderId) {
        OrderItemEntity entity = orderItemMapper.toEntity(dto);
        entity.setId(itemId);
        entity.setOrderId(orderId);
        return entity;
    }

    private OrderItemDto saveAndConvert(OrderItemEntity entity) {
        return orderItemMapper.toDto(repoService.save(entity));
    }

    private void validateItemBelongsToOrder(OrderItemEntity item, Long orderId) {
        if (!item.getOrderId().equals(orderId)) {
            throw new IllegalArgumentException("Order item does not belong to the specified order");
        }
    }

    private List<OrderItemDto> convertList(List<OrderItemEntity> entities) {
        return entities.stream()
                .map(orderItemMapper::toDto)
                .collect(Collectors.toList());
    }
}
