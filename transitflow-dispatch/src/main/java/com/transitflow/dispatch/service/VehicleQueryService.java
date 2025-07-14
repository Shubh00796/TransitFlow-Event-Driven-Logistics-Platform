package com.transitflow.dispatch.service;


import com.transitflow.common.dtos.disptach.VehicleDto;
import com.transitflow.common.enmus.VehicleStatus;
import com.transitflow.common.enmus.VehicleType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface VehicleQueryService {
    VehicleDto getVehicleById(Long id);
    List<VehicleDto> getVehiclesByType(VehicleType type);
    Page<VehicleDto> getVehiclesByStatus(VehicleStatus status, Pageable pageable);
    List<VehicleDto> searchAvailableVehicles(String location);
    long countVehiclesByStatus(VehicleStatus status);
}
