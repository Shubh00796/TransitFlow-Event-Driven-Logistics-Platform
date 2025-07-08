package com.transistflow.commans.dtos.order;


import lombok.Data;

@Data
public class OrderItemDto {
    private Long productId;
    private Integer quantity;
    private Double price;
}
