package com.carrefour.infrastructure.h2.repoimpl;

import com.carrefour.domain.model.dto.model.DeliveryDto;
import com.carrefour.domain.model.dto.model.DeliveryOptionEnum;
import com.carrefour.domain.repository.DeliveryService;
import com.carrefour.infrastructure.h2.entity.Delivery;
import com.carrefour.infrastructure.h2.mapper.DeliveryMapper;
import com.carrefour.infrastructure.h2.repo.DeliveryRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import lombok.Data;

import java.math.BigDecimal;

@Component
@Data
@AllArgsConstructor
public class DeliveryServiceImpl implements DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final DeliveryMapper deliveryMapper;

    @Override
    public Mono<DeliveryDto> saveDelivery(DeliveryOptionEnum deliveryOption, BigDecimal customerId) {

        Mono<Delivery> byDeliveryOptionAndCustomerId = deliveryRepository.findByDeliveryOptionAndCustomerId(
            deliveryOption.getValue(), customerId.longValue()
        );
        return byDeliveryOptionAndCustomerId.map(
            deliveryMapper::deliveryToDeliveryDto
        ).switchIfEmpty(
            saveDeliveryH2(DeliveryDto.builder().deliveryOption(deliveryOption).customerId(customerId.longValue()).build())
        );
    }


    private Mono<DeliveryDto> saveDeliveryH2(DeliveryDto deliveryDtoH2) {

        Delivery delivery = Delivery.builder()
            .customerId(deliveryDtoH2.getCustomerId())
            .deliveryOption(com.carrefour.infrastructure.h2.entity.DeliveryOptionEnum.fromValue(deliveryDtoH2.getDeliveryOption().getValue()))
            .build();
        Mono<Delivery> deliverySaveMono = deliveryRepository.save(delivery);
        return deliverySaveMono.map( deliverySave -> {
            deliveryDtoH2.setId(deliverySave.getId());
            return deliveryDtoH2;
        });
    }
}
