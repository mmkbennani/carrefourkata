package com.carrefour.domain.repository;

import com.carrefour.domain.model.dto.model.DeliveryDto;

import com.carrefour.domain.model.dto.model.DeliveryOptionEnum;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

public interface DeliveryService {


    Mono<DeliveryDto> saveDelivery(DeliveryOptionEnum deliveryOption, BigDecimal customerId);
}
