package com.transitflow.dispatch.repository;


import com.transitflow.dispatch.domain.Vehicle;
import com.transitflow.common.enmus.VehicleStatus;
import com.transitflow.common.enmus.VehicleType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    List<Vehicle> findByType(VehicleType type);

    Page<Vehicle> findAllByStatus(VehicleStatus status, Pageable pageable);

    // Search for available vehicles near a location
    @Query("SELECT v FROM Vehicle v WHERE v.status = :status AND LOWER(v.currentLocation) LIKE LOWER(CONCAT('%', :location, '%'))")
    List<Vehicle> searchAvailableByLocation(@Param("status") VehicleStatus status, @Param("location") String location);

    long countByStatus(VehicleStatus status);

    // Find all idle (available) vehicles ordered by last update
    List<Vehicle> findByStatusOrderByUpdatedAtDesc(VehicleStatus status);
}
