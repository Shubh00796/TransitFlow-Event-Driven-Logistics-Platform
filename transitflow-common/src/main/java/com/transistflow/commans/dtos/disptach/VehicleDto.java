package com.transistflow.commans.dtos.disptach;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VehicleDto {
    private Long id;
    private String type; // e.g., TRUCK
    private Integer capacity;
    private String status;
    private String currentLocation;
}
