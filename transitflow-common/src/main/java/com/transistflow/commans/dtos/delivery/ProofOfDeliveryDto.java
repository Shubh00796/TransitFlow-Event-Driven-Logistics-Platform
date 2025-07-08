package com.transistflow.commans.dtos.delivery;


import lombok.Data;

@Data
public class ProofOfDeliveryDto {
    private Long shipmentId;
    private String recipientName;
    private String deliveryPhotoUrl; // if applicable
    private String notes;
}
