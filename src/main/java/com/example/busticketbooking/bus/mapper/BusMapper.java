package com.example.busticketbooking.bus.mapper;

import com.example.busticketbooking.bus.dto.BusRequest;
import com.example.busticketbooking.bus.dto.BusResponse;
import com.example.busticketbooking.bus.entity.Bus;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BusMapper {
    Bus toEntity(BusRequest dto);

    BusResponse toResponseDto(Bus entity);
}
