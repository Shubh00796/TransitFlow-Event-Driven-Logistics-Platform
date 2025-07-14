package com.transitflow.order.reposiotries.data_access;


import com.transistflow.commans.enmus.OrderStatus;
import com.transistflow.commans.exceptions.ResourceNotFoundException;
import com.transitflow.order.domain.OrderEntity;
import com.transitflow.order.reposiotries.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class OrderRepoService {

    private final OrderRepository orderRepository;

    public OrderEntity findById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + id));
    }

    public Page<OrderEntity> findByCustomerId(Long customerId, Pageable pageable) {
        return orderRepository.findByCustomerId(customerId, pageable);
    }

    public Slice<OrderEntity> findByStatus(OrderStatus status, Pageable pageable) {
        return orderRepository.findByStatus(status, pageable);
    }

    public List<OrderEntity> findTop100ByStatus(OrderStatus status) {
        return orderRepository.findTop100ByStatusOrderByUpdatedAtDesc(status);
    }

    public Stream<OrderEntity> streamCreatedBefore(Instant cutoff) {
        return orderRepository.streamAllByCreatedAtBefore(cutoff);
    }

    public List<OrderEntity> findLatest50Cancelled() {
        return orderRepository.findLatest50CancelledNative();
    }

    public OrderEntity save(OrderEntity order) {
        return orderRepository.save(order);
    }

    public void delete(OrderEntity order) {
        orderRepository.delete(order);
    }
}
