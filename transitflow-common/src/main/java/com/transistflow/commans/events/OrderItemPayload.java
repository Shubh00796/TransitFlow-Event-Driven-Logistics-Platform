package com.transistflow.commans.events;


import lombok.Data;

@Data
public class OrderItemPayload {
    private Long productId;
    private Integer quantity;
    private Double price;
}
