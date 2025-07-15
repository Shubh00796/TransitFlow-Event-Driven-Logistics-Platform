package com.transitflow.dispatch.service.business_logic;

import com.transitflow.common.dtos.disptach.VehicleDto;
import com.transitflow.common.enmus.VehicleStatus;
import com.transitflow.common.enmus.VehicleType;
import com.transitflow.dispatch.mappers.DispatchMapper;
import com.transitflow.dispatch.repository.data_access_layer.VehicleRepoService;
import com.transitflow.dispatch.service.VehicleQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class VehicleQueryServiceImpl implements VehicleQueryService {

    private final VehicleRepoService repoService;
    private final DispatchMapper mapper;

    @Override
    public VehicleDto getVehicleById(Long id) {
        return map(mapper::toVehicleDto, repoService.findById(id));
    }

    @Override
    public List<VehicleDto> getVehiclesByType(VehicleType type) {
        return mapList(mapper::toVehicleDto, repoService.findByType(type));
    }

    @Override
    public Page<VehicleDto> getVehiclesByStatus(VehicleStatus status, Pageable pageable) {
        return repoService.findAllByStatus(status, pageable)
                .map(mapper::toVehicleDto);
    }

    @Override
    public List<VehicleDto> searchAvailableVehicles(String location) {
        return mapList(
                mapper::toVehicleDto,
                repoService.searchAvailableByLocation(VehicleStatus.AVAILABLE, location)
        );
    }

    @Override
    public long countVehiclesByStatus(VehicleStatus status) {
        return repoService.countByStatus(status);
    }

    //****************private_helpers***************

    private <T, R> R map(Function<T, R> mapper, T input) {
        return mapper.apply(input);
    }

    private <T, R> List<R> mapList(Function<T, R> mapper, Collection<T> input) {
        return input.stream()
                .map(mapper)
                .collect(Collectors.toList());
    }
}
