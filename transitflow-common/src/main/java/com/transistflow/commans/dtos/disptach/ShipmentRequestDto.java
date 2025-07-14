package com.transistflow.commans.dtos.disptach;


import com.transistflow.commans.enmus.ShipmentStatus;
import lombok.Data;

@Data
public class ShipmentRequestDto {
    private Long orderId;
    private Long vehicleId;
    private ShipmentStatus status;

}
