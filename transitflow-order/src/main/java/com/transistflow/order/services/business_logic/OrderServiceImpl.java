package com.transistflow.order.services.business_logic;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.transistflow.commans.dtos.order.OrderItemDto;
import com.transistflow.commans.dtos.order.OrderRequestDto;
import com.transistflow.commans.dtos.order.OrderResponseDto;
import com.transistflow.commans.dtos.order.OrderUpdateRequestDto;
import com.transistflow.commans.enmus.OrderStatus;
import com.transistflow.commans.events.OrderCancelledEvent;
import com.transistflow.commans.events.OrderCreatedEvent;
import com.transistflow.commans.events.OrderStatusChangedEvent;
import com.transistflow.order.domain.OrderEntity;
import com.transistflow.order.domain.OutboxEvent;
import com.transistflow.order.mappers.OrderMapper;
import com.transistflow.order.reposiotries.OutboxEventRepository;
import com.transistflow.order.reposiotries.data_access.OrderRepoService;
import com.transistflow.order.services.OrderItemService;
import com.transistflow.order.services.OrderService;
import com.transistflow.order.utils.OrderValidationUtil;
import com.transistflow.order.utils.OutboxEventFactoryUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderMapper mapper;
    private final OrderRepoService repoService;
    private final OrderValidationUtil validationUtil;
    private final ObjectMapper objectMapper;
    private final OutboxEventRepository outboxRepo;
    private final OutboxEventFactoryUtil outboxEventFactory;
    private final OrderItemService orderItemService;


    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public OrderResponseDto createOrder(OrderRequestDto request) {
        validationUtil.validateCreateOrder(request);
        OrderEntity orderEntity = mapper.toEntity(request);
        OrderEntity savedOrder = repoService.save(orderEntity);
        saveOrderItemsUsingFK(request, savedOrder);

        // ✅ Step 4: Publish event
        OrderCreatedEvent createdEvent = mapper.toCreatedEvent(savedOrder);
        OutboxEvent outboxEvent = outboxEventFactory.fromEvent(createdEvent, savedOrder.getId().toString(), "ORDER");
        outboxRepo.save(outboxEvent);

        // ✅ Step 5: Build response manually including items
        OrderResponseDto response = mapper.toResponse(savedOrder);
        List<OrderItemDto> items = orderItemService.getAllItemsByOrder(savedOrder.getId());
        response.setItems(items);

        return response;
    }


    private void saveOrderItemsUsingFK(OrderRequestDto request, OrderEntity savedOrder) {
        if (request.getItems() != null && !request.getItems().isEmpty()) {
            request.getItems().forEach(itemDto -> {
                orderItemService.createOrderItem(savedOrder.getId(), itemDto);
            });
        }
    }


    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public OrderResponseDto updateOrder(Long orderId, OrderUpdateRequestDto request) {
        validationUtil.validateUpdateOrder(request);

        OrderEntity existing = repoService.findById(orderId);
        String oldStatus = existing.getStatus().name();
        mapper.updateEntityFromRequest(existing, request);
        OrderEntity updated = repoService.save(existing);

        // choose correct event
        Object payload = createUpdateEventPayload(orderId, oldStatus, updated);
        OutboxEvent event = outboxEventFactory.fromEvent(payload, orderId.toString(), "ORDER");
        outboxRepo.save(event);

        log.info("Updated order {} ({} -> {})",
                orderId, oldStatus, updated.getStatus());
        return mapper.toResponse(updated);
    }

    @Override
    @Transactional
    public void deleteOrder(Long orderId) {
        OrderEntity toDelete = repoService.findById(orderId);
        repoService.delete(toDelete);

        // optional: publish cancellation event
        OrderCancelledEvent cancelled = new OrderCancelledEvent(orderId, "Order deleted", Instant.now());
        OutboxEvent event = outboxEventFactory.fromEvent(cancelled, orderId.toString(), "ORDER");
        outboxRepo.save(event);

        log.info("Deleted order {}", orderId);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponseDto getOrderById(Long orderId) {
        return mapToResponse(repoService.findById(orderId));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponseDto> getOrdersByCustomer(Long customerId, Pageable pageable) {
        return mapPage(repoService.findByCustomerId(customerId, pageable));
    }

    @Override
    @Transactional(readOnly = true)
    public Slice<OrderResponseDto> getOrdersByStatus(String status, Pageable pageable) {
        return mapSlice(repoService.findByStatus(toOrderStatus(status), pageable));
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDto> getTop100ByStatus(String status) {
        return mapList(repoService.findTop100ByStatus(toOrderStatus(status)));
    }

    @Override
    @Transactional(readOnly = true)
    public Stream<OrderResponseDto> streamOrdersCreatedBefore(Instant cutoff) {
        return mapStream(repoService.streamCreatedBefore(cutoff));
    }


    /*--- helpers ---*/

    /**
     * Chooses between a cancellation event and a status-changed event.
     */
    private Object createUpdateEventPayload(Long orderId,
                                            String oldStatus,
                                            OrderEntity updated) {
        String newStatus = updated.getStatus().name();
        Instant now = Instant.now();

        if (OrderStatus.CANCELLED.name().equalsIgnoreCase(newStatus)) {
            return new OrderCancelledEvent(orderId, "Status changed to CANCELLED", now);
        }
        return new OrderStatusChangedEvent(orderId, oldStatus, newStatus, now);
    }

    private OrderStatus toOrderStatus(String status) {
        try {
            return OrderStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid order status: " + status);
        }
    }

    private OrderResponseDto mapToResponse(OrderEntity entity) {
        return mapper.toResponse(entity);
    }

    private Page<OrderResponseDto> mapPage(Page<OrderEntity> page) {
        return page.map(mapper::toResponse);
    }

    private Slice<OrderResponseDto> mapSlice(Slice<OrderEntity> slice) {
        return slice.map(mapper::toResponse);
    }

    private List<OrderResponseDto> mapList(List<OrderEntity> list) {
        return list.stream().map(mapper::toResponse).toList();
    }

    private Stream<OrderResponseDto> mapStream(Stream<OrderEntity> stream) {
        return stream.map(mapper::toResponse);
    }

}
