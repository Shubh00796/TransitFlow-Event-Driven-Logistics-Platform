package com.transistflow.commans.dtos.order;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class OrderItemDto {
    private Long productId;
    private Integer quantity;
    private Double price;
}
