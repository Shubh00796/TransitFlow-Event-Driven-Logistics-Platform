package com.transistflow.order.utils;


import com.transistflow.commans.dtos.order.OrderItemDto;
import com.transistflow.commans.dtos.order.OrderRequestDto;
import com.transistflow.commans.dtos.order.OrderUpdateRequestDto;
import com.transistflow.order.reposiotries.data_access.OrderItemRepoService;
import com.transistflow.order.reposiotries.data_access.OrderRepoService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class OrderValidationUtil {

    private final OrderRepoService orderRepoService;
    private final OrderItemRepoService orderItemRepoService;

    private final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = validatorFactory.getValidator();

    // ─── Order Validations ─────────────────────────────────────────────────────

    /**
     * Validates an OrderRequestDto for creation:
     *   1) Non-null DTO
     *   2) JSR‑380 constraints on the DTO and its items
     */
    public void validateCreateOrder(OrderRequestDto dto) {
        Objects.requireNonNull(dto, "OrderRequestDto cannot be null");
        validateJsrViolations(dto);
        dto.getItems().forEach(this::validateJsrViolations);
    }

    /**
     * Validates an OrderUpdateRequestDto:
     *   1) Non-null DTO
     *   2) JSR‑380 constraints on the DTO
     *   3) Existence of the Order being updated
     */
    public void validateUpdateOrder(OrderUpdateRequestDto dto) {
        Objects.requireNonNull(dto, "OrderUpdateRequestDto cannot be null");
        validateJsrViolations(dto);
    }

    // ─── OrderItem Validations ────────────────────────────────────────────────

    /**
     * Validates an OrderItemDto for creation under a given Order:
     *   1) Non-null DTO
     *   2) JSR‑380 constraints on the DTO
     *   3) Existence of the parent Order
     */
    public void validateCreateOrderItem(Long orderId, OrderItemDto dto) {
        Objects.requireNonNull(dto, "OrderItemDto cannot be null");
        validateJsrViolations(dto);
        orderRepoService.findById(orderId);
    }

    /**
     * Validates an OrderItemDto for update under a given Order and item:
     *   1) Non-null DTO
     *   2) JSR‑380 constraints on the DTO
     *   3) Existence of the parent Order and the OrderItem
     */
    public void validateUpdateOrderItem(Long orderId, Long itemId, OrderItemDto dto) {
        Objects.requireNonNull(dto, "OrderItemDto cannot be null");
        validateJsrViolations(dto);
        orderRepoService.findById(orderId);
        orderItemRepoService.findById(itemId);
    }

    // ─── Shared Helper ────────────────────────────────────────────────────────

    private <T> void validateJsrViolations(T dto) {
        Set<ConstraintViolation<T>> violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            throw new IllegalArgumentException("Validation failed: " + violations);
        }
    }
}
