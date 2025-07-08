package com.transistflow.commans.dtos.disptach;


import lombok.Data;

@Data
public class ShipmentRequestDto {
    private Long orderId;
    private Long vehicleId;
}
