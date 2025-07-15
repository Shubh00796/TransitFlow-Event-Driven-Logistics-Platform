package com.transitflow.dispatch.controllers;


import com.transitflow.common.dtos.ApiResponse;
import com.transitflow.common.dtos.disptach.VehicleDto;
import com.transitflow.common.enmus.VehicleStatus;
import com.transitflow.common.enmus.VehicleType;
import com.transitflow.dispatch.service.VehicleCommandService;
import com.transitflow.dispatch.service.VehicleQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleCommandService commandService;
    private final VehicleQueryService queryService;

    // Create Vehicle
    @PostMapping
    public ResponseEntity<ApiResponse<VehicleDto>> createVehicle(@RequestBody VehicleDto dto) {
        VehicleDto created = commandService.createVehicle(dto);
        return ResponseEntity.ok(ApiResponse.success("Vehicle created successfully", created));
    }

    // Update Vehicle
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<VehicleDto>> updateVehicle(@PathVariable Long id, @RequestBody VehicleDto dto) {
        VehicleDto updated = commandService.updateVehicle(id, dto);
        return ResponseEntity.ok(ApiResponse.success("Vehicle updated successfully", updated));
    }

    // Retire Vehicle
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> retireVehicle(@PathVariable Long id) {
        commandService.retireVehicle(id);
        return ResponseEntity.ok(ApiResponse.success("Vehicle retired successfully", null));
    }

    // Get by ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<VehicleDto>> getById(@PathVariable Long id) {
        VehicleDto dto = queryService.getVehicleById(id);
        return ResponseEntity.ok(ApiResponse.success("Vehicle fetched successfully", dto));
    }

    // Get by Type
    @GetMapping("/type/{type}")
    public ResponseEntity<ApiResponse<List<VehicleDto>>> getByType(@PathVariable VehicleType type) {
        List<VehicleDto> list = queryService.getVehiclesByType(type);
        return ResponseEntity.ok(ApiResponse.success("Vehicles fetched by type", list));
    }

    // Get by Status with Pagination
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<Page<VehicleDto>>> getByStatus(
            @PathVariable VehicleStatus status,
            Pageable pageable) {
        Page<VehicleDto> page = queryService.getVehiclesByStatus(status, pageable);
        return ResponseEntity.ok(ApiResponse.success("Vehicles fetched by status", page));
    }

    // Search Available by Location
    @GetMapping("/available")
    public ResponseEntity<ApiResponse<List<VehicleDto>>> searchAvailable(@RequestParam String location) {
        List<VehicleDto> available = queryService.searchAvailableVehicles(location);
        return ResponseEntity.ok(ApiResponse.success("Available vehicles fetched", available));
    }

    // Count by Status
    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Long>> countByStatus(@RequestParam VehicleStatus status) {
        long count = queryService.countVehiclesByStatus(status);
        return ResponseEntity.ok(ApiResponse.success("Vehicle count by status fetched", count));
    }
}
