package com.transistflow.order.reposiotries.data_access;


import com.transistflow.commans.exceptions.ResourceNotFoundException;
import com.transistflow.order.domain.OrderItemEntity;
import com.transistflow.order.reposiotries.OrderItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class OrderItemRepoService {

    private final OrderItemRepository orderItemRepository;

    public OrderItemEntity findById(Long itemId) {
        return orderItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Order item not found with ID: " + itemId));
    }

    public Page<OrderItemEntity> findByOrderId(Long orderId, Pageable pageable) {
        return orderItemRepository.findByOrderId(orderId, pageable);
    }

    public List<OrderItemEntity> findAllByOrderId(Long orderId) {
        return orderItemRepository.findAllByOrderId(orderId);
    }

    public Stream<OrderItemEntity> streamByOrderId(Long orderId) {
        return orderItemRepository.streamByOrderId(orderId);
    }

    public OrderItemEntity save(OrderItemEntity item) {
        return orderItemRepository.save(item);
    }

    public void delete(OrderItemEntity item) {
        orderItemRepository.delete(item);
    }
}
