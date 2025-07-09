package com.transistflow.order.controllers;

import com.transistflow.commans.dtos.ApiResponse;
import com.transistflow.commans.dtos.order.OrderRequestDto;
import com.transistflow.commans.dtos.order.OrderResponseDto;
import com.transistflow.commans.dtos.order.OrderUpdateRequestDto;
import com.transistflow.order.services.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponseDto>> createOrder(
            @Valid @RequestBody OrderRequestDto request) {

        OrderResponseDto created = orderService.createOrder(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Order created successfully", created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResponseDto>> updateOrder(
            @PathVariable Long id,
            @Valid @RequestBody OrderUpdateRequestDto request) {

        OrderResponseDto updated = orderService.updateOrder(id, request);
        return ResponseEntity.ok(ApiResponse.success("Order updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.ok(ApiResponse.success("Order deleted successfully", null));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResponseDto>> getOrderById(@PathVariable Long id) {
        OrderResponseDto dto = orderService.getOrderById(id);
        return ResponseEntity.ok(ApiResponse.success("Order fetched", dto));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<ApiResponse<Page<OrderResponseDto>>> getOrdersByCustomer(
            @PathVariable Long customerId,
            Pageable pageable) {

        Page<OrderResponseDto> page = orderService.getOrdersByCustomer(customerId, pageable);
        return ResponseEntity.ok(ApiResponse.success("Customer orders fetched", page));
    }

    @GetMapping("/status")
    public ResponseEntity<ApiResponse<Slice<OrderResponseDto>>> getOrdersByStatus(
            @RequestParam String status,
            Pageable pageable) {

        Slice<OrderResponseDto> slice = orderService.getOrdersByStatus(status, pageable);
        return ResponseEntity.ok(ApiResponse.success("Orders by status fetched", slice));
    }

    @GetMapping("/top100")
    public ResponseEntity<ApiResponse<List<OrderResponseDto>>> getTop100ByStatus(
            @RequestParam String status) {

        List<OrderResponseDto> list = orderService.getTop100ByStatus(status);
        return ResponseEntity.ok(ApiResponse.success("Top 100 orders fetched", list));
    }

    @GetMapping("/stream")
    public ResponseEntity<ApiResponse<List<OrderResponseDto>>> streamOrdersBefore(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant cutoff) {

        List<OrderResponseDto> list = orderService.streamOrdersCreatedBefore(cutoff)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success("Streamed orders fetched", list));
    }


}
