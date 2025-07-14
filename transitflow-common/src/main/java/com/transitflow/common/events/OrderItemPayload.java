package com.transitflow.common.events;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderItemPayload {
    private Long productId;
    private Integer quantity;
    private Double price;
}
