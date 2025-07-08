package com.transistflow.commans.dtos.disptach;


import lombok.Data;

@Data
public class VehicleDto {
    private Long id;
    private String type; // e.g., TRUCK
    private Integer capacity;
    private String status;
    private String currentLocation;
}
