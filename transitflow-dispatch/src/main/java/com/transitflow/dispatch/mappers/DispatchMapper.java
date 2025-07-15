package com.transitflow.dispatch.mappers;


import com.transitflow.common.dtos.disptach.ShipmentRequestDto;
import com.transitflow.common.dtos.disptach.ShipmentResponseDto;
import com.transitflow.common.dtos.disptach.VehicleDto;
import com.transitflow.dispatch.domain.Shipment;
import com.transitflow.dispatch.domain.Vehicle;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DispatchMapper {

    // Shipment Mappings
    Shipment toShipment(ShipmentRequestDto dto);

    ShipmentResponseDto toShipmentResponseDto(Shipment shipment);

    // Vehicle Mappings
    VehicleDto toVehicleDto(Vehicle vehicle);

    Vehicle fromVehicleDto(VehicleDto dto);

}
