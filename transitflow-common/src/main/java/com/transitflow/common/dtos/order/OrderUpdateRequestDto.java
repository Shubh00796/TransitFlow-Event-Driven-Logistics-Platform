package com.transitflow.common.dtos.order;


import lombok.Data;

@Data
public class OrderUpdateRequestDto {
    private String destination;
    private String status; // e.g., CANCELLED
}

