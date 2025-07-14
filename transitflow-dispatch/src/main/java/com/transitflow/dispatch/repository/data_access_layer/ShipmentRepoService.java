package com.transitflow.dispatch.repository.data_access_layer;


import com.transistflow.commans.enmus.ShipmentStatus;
import com.transistflow.commans.exceptions.ResourceNotFoundException;
import com.transitflow.dispatch.domain.Shipment;
import com.transitflow.dispatch.repository.ShipmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ShipmentRepoService {

    private final ShipmentRepository shipmentRepository;

    public Shipment findById(Long id) {
        return shipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment not found with ID: " + id));
    }

    public List<Shipment> findByOrderId(Long orderId) {
        return shipmentRepository.findByOrderId(orderId);
    }

    public List<Shipment> findByVehicleId(Long vehicleId) {
        return shipmentRepository.findByVehicleId(vehicleId);
    }

    public Page<Shipment> findAllByStatus(ShipmentStatus status, Pageable pageable) {
        return shipmentRepository.findAllByStatus(status, pageable);
    }

    public Stream<Shipment> streamDeliveredBetween(Instant from, Instant to) {
        return shipmentRepository.streamDeliveredBetween(from, to);
    }

    public Optional<Shipment> findByOrderIdAndVehicleId(Long orderId, Long vehicleId) {
        return shipmentRepository.findByOrderIdAndVehicleId(orderId, vehicleId);
    }

    public long countByStatus(ShipmentStatus status) {
        return shipmentRepository.countByStatus(status);
    }

    public List<Shipment> findTop10ByStatusOrderByDispatchedAtDesc(ShipmentStatus status) {
        return shipmentRepository.findTop10ByStatusOrderByDispatchedAtDesc(status);
    }

    public Shipment save(Shipment shipment) {
        return shipmentRepository.save(shipment);
    }

    public void delete(Shipment shipment) {
        shipmentRepository.delete(shipment);
    }
}
