package com.transitflow.dispatch.repository.data_access_layer;


import com.transistflow.commans.enmus.VehicleStatus;
import com.transistflow.commans.enmus.VehicleType;
import com.transistflow.commans.exceptions.ResourceNotFoundException;
import com.transitflow.dispatch.domain.Vehicle;
import com.transitflow.dispatch.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VehicleRepoService {

    private final VehicleRepository vehicleRepository;

    public Vehicle findById(Long id) {
        return vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with ID: " + id));
    }

    public List<Vehicle> findByType(VehicleType type) {
        return vehicleRepository.findByType(type);
    }

    public Page<Vehicle> findAllByStatus(VehicleStatus status, Pageable pageable) {
        return vehicleRepository.findAllByStatus(status, pageable);
    }

    public List<Vehicle> searchAvailableByLocation(VehicleStatus status, String location) {
        return vehicleRepository.searchAvailableByLocation(status, location);
    }

    public long countByStatus(VehicleStatus status) {
        return vehicleRepository.countByStatus(status);
    }

    public List<Vehicle> findByStatusOrderByUpdatedAtDesc(VehicleStatus status) {
        return vehicleRepository.findByStatusOrderByUpdatedAtDesc(status);
    }

    public Vehicle save(Vehicle vehicle) {
        return vehicleRepository.save(vehicle);
    }

    public void delete(Vehicle vehicle) {
        vehicleRepository.delete(vehicle);
    }
}
