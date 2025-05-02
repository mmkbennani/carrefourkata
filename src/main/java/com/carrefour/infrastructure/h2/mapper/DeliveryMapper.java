package com.carrefour.infrastructure.h2.mapper;

import com.carrefour.domain.model.dto.model.DeliveryDto;
import com.carrefour.infrastructure.h2.entity.Delivery;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DeliveryMapper {

    DeliveryDto deliveryToDeliveryDto(Delivery delivery);
}
