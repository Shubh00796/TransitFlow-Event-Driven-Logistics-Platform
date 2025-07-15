package com.transitflow.dispatch.service.business_logic;

import com.transitflow.common.dtos.disptach.VehicleDto;
import com.transitflow.common.enmus.VehicleStatus;
import com.transitflow.dispatch.domain.Vehicle;
import com.transitflow.dispatch.mappers.DispatchMapper;
import com.transitflow.dispatch.repository.data_access_layer.VehicleRepoService;
import com.transitflow.dispatch.service.VehicleCommandService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class VehicleCommandServiceImpl implements VehicleCommandService {

    private final VehicleRepoService vehicleRepoService;
    private final DispatchMapper dispatchMapper;

    @Override
    public VehicleDto createVehicle(VehicleDto dto) {
        Vehicle vehicle = dispatchMapper.fromVehicleDto(dto);
        vehicle.setStatus(VehicleStatus.AVAILABLE);
        return dispatchMapper.toVehicleDto(vehicleRepoService.save(vehicle));
    }
    @Override
    public VehicleDto updateVehicle(Long id, VehicleDto dto) {
        Vehicle existing = vehicleRepoService.findById(id);

        updateIfNotNull(dto.getType(), existing::setType);
        updateIfNotNull(dto.getStatus(), existing::setStatus);
        updateIfNotNull(dto.getCapacity(), existing::setCapacity);
        updateIfNotNull(dto.getCurrentLocation(), existing::setCurrentLocation);

        Vehicle updated = vehicleRepoService.save(existing);
        return dispatchMapper.toVehicleDto(updated);
    }



    @Override
    public void retireVehicle(Long id) {
        Vehicle vehicle = vehicleRepoService.findById(id);
        vehicle.setStatus(VehicleStatus.MAINTENANCE);
        vehicleRepoService.save(vehicle);
    }



    //***********PRIVATE_HELPERS***********

    private <T> void updateIfNotNull(T value, java.util.function.Consumer<T> setter) {
        if (value != null) setter.accept(value);
    }



}
