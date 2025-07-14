package com.transitflow.dispatch.service;



import com.transistflow.commans.dtos.disptach.VehicleDto;

public interface VehicleCommandService {
    VehicleDto createVehicle(VehicleDto dto);
    VehicleDto updateVehicle(Long id, VehicleDto dto);
    void retireVehicle(Long id);
}
