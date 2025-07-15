package com.transitflow.common.dtos.disptach;


import com.transitflow.common.enmus.VehicleStatus;
import com.transitflow.common.enmus.VehicleType;
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
    private VehicleType type; // e.g., TRUCK
    private Integer capacity;
    private VehicleStatus status;
    private String currentLocation;
}
