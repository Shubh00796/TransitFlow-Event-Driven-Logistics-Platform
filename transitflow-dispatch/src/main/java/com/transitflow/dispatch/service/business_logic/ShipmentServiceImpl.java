package com.transitflow.dispatch.service.business_logic;

import com.transitflow.common.dtos.disptach.ShipmentRequestDto;
import com.transitflow.common.dtos.disptach.ShipmentResponseDto;
import com.transitflow.common.enmus.ShipmentStatus;
import com.transitflow.common.enmus.VehicleStatus;
import com.transitflow.common.events.ShipmentDispatchedEvent;
import com.transitflow.common.outbox.DomainEventPublisher;
import com.transitflow.dispatch.domain.Shipment;
import com.transitflow.dispatch.domain.Vehicle;
import com.transitflow.dispatch.mappers.DispatchMapper;
import com.transitflow.dispatch.repository.data_access_layer.ShipmentRepoService;
import com.transitflow.dispatch.repository.data_access_layer.VehicleRepoService;
import com.transitflow.dispatch.service.ShipmentService;
import com.transitflow.dispatch.utils.ShipmentEventFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShipmentServiceImpl implements ShipmentService {
    private final ShipmentRepoService shipmentRepoService;
    private final DispatchMapper mapper;
    private final VehicleRepoService vehicleRepoService;
    private final DomainEventPublisher domainEventPublisher;
    private final ShipmentEventFactory shipmentEventFactory;

    /**
     * Creates and dispatches a new shipment and queues an outbox event
     *
     * @param request the shipment request DTO
     * @return the saved shipment response DTO
     * @throws IllegalArgumentException if required data is missing
     */
    @Override
    @Transactional
    public ShipmentResponseDto createShipment(ShipmentRequestDto request) {
        validateRequest(request);
        validateShipmentRequest(request);

        Shipment shipment = buildDispatchedShipment(request);
        Shipment savedShipment = shipmentRepoService.save(shipment);

        ShipmentDispatchedEvent event = shipmentEventFactory.shipmentDispatchedEvent(savedShipment);
        domainEventPublisher.publish(event, savedShipment.getId().toString(), "Shipment");

        return mapper.toShipmentResponseDto(savedShipment);
    }


    @Override
    public ShipmentResponseDto getShipmentById(Long id) {
        return mapToResponse(fetchOrThrow(id));
    }

    @Override
    public List<ShipmentResponseDto> getShipmentsByOrderId(Long orderId) {
        return mapList(shipmentRepoService.findByOrderId(orderId));
    }

    @Override
    public List<ShipmentResponseDto> getShipmentsByVehicleId(Long vehicleId) {
        return mapList(shipmentRepoService.findByVehicleId(vehicleId));
    }

    @Override
    public Page<ShipmentResponseDto> getShipmentsByStatus(ShipmentStatus status, Pageable pageable) {
        return mapPage(shipmentRepoService.findAllByStatus(status, pageable));
    }

    @Override
    public List<ShipmentResponseDto> getRecentDispatchedShipments(ShipmentStatus status) {
        return mapList(shipmentRepoService.findTop10ByStatusOrderByDispatchedAtDesc(status));
    }

    @Override
    @Transactional
    public void markAsDelivered(Long shipmentId, Instant deliveredAt) {
        Shipment shipment = fetchOrThrow(shipmentId);
        if (checkIfShipmentIsDeliverdOrNot(shipmentId, shipment)) return;
        shipment.setStatus(ShipmentStatus.DELIVERED);
        shipment.setDeliveredAt(Optional.ofNullable(deliveredAt).orElseGet(Instant::now));

        shipmentRepoService.save(shipment);
    }


    //**************PRIVATE-HELPERS*********************

    private void validateRequest(ShipmentRequestDto request) {
        Optional.ofNullable(request).orElseThrow(() -> new IllegalArgumentException("Shipment request must not be null"));

        Stream.of(request.getOrderId(), request.getVehicleId())
                .filter(Objects::isNull)
                .findFirst()
                .ifPresent(missing -> {
                    throw new IllegalArgumentException("Order ID and Vehicle ID must not be null");
                });
    }

    private Shipment buildDispatchedShipment(ShipmentRequestDto request) {
        Shipment shipment = mapper.toShipment(request);
        shipment.setStatus(ShipmentStatus.DISPATCHED);
        shipment.setDispatchedAt(Instant.now());
        return shipment;
    }


    private Shipment fetchOrThrow(Long shipmentId) {
        return shipmentRepoService.findById(shipmentId);
    }

    private ShipmentResponseDto mapToResponse(Shipment shipment) {
        return mapper.toShipmentResponseDto(shipment);
    }

    private List<ShipmentResponseDto> mapList(List<Shipment> shipments) {
        return shipments.stream()
                .map(this::mapToResponse)
                .toList();
    }


    private void validateShipmentRequest(ShipmentRequestDto request) {
        validate(request.getVehicleId() != null, "Vehicle ID must not be null.");
        Vehicle vehicle = vehicleRepoService.findById(request.getVehicleId());

        validate(vehicle.getStatus() == VehicleStatus.AVAILABLE,
                () -> "Vehicle is not available for shipment. Current status: " + vehicle.getStatus());

    }


    private Page<ShipmentResponseDto> mapPage(Page<Shipment> page) {
        return page.map(this::mapToResponse);
    }

    private void validate(boolean condition, String message) {
        if (!condition) {
            throw new IllegalArgumentException(message);
        }
    }

    private void validate(boolean condition, Supplier<String> messageSupplier) {
        if (!condition) {
            throw new IllegalArgumentException(messageSupplier.get());
        }
    }

    private static boolean checkIfShipmentIsDeliverdOrNot(Long shipmentId, Shipment shipment) {
        if (shipment.getStatus() == ShipmentStatus.DELIVERED) {
            log.info("Shipment {} already marked as delivered", shipmentId);
            return true;
        }
        return false;
    }


}
