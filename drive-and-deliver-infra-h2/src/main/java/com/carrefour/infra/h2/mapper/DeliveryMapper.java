package com.carrefour.infra.h2.mapper;

import com.carrefour.domain.model.dto.model.DeliveryDto;
import com.carrefour.infra.h2.entity.Delivery;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DeliveryMapper {

    DeliveryDto deliveryToDeliveryDto(Delivery delivery);
}
