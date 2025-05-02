package com.carrefour.application;

import com.carrefour.domain.model.dto.model.ChooseOptionResource;
import com.carrefour.domain.model.dto.model.DeliveryDto;
import com.carrefour.domain.model.dto.model.DeliveryOptionEnum;
import com.carrefour.domain.model.dto.model.TimeSlotDto;
import com.carrefour.domain.repository.DeliveryService;
import com.carrefour.domain.repository.KafkaService;
import com.carrefour.domain.repository.TimeSlotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;


@Component
@Slf4j
@RequiredArgsConstructor
public class DriveManagementImpl implements DriveManagement {


    private final DeliveryService deliveryService;
    private final TimeSlotService timeSlotService;
    private final KafkaService kafkaService;

    @Override
    public Mono<DeliveryDto> chooseDeliveryOption(Mono<ChooseOptionResource> deliveryOptionMono) {
        return deliveryOptionMono.flatMap(
            deliveryOption -> {
                log.info("Choose delivery option  {} for customer {}", deliveryOption.getDeliveryOption(), deliveryOption.getCustomerId());
                return deliveryService.saveDelivery(DeliveryOptionEnum.valueOf(deliveryOption.getDeliveryOption().getValue()), deliveryOption.getCustomerId())
                    .doOnNext(deliveryOptionForKafka ->
                        {
                            log.info("Preparing to send Kafka event for deliveryOption: {}", deliveryOptionForKafka);
                            kafkaService.publishEvent("DeliveryOptionChosen", deliveryOptionForKafka);
                        }
                    );
            }
        );
    }

    @Override
    public Mono<TimeSlotDto> bookTimeSlot(Mono<TimeSlotDto> timeSlotDtoMono) {
        return timeSlotDtoMono.flatMap(
            timeSlotDtoDto -> {
                log.info("Book time slot for customer {}", timeSlotDtoDto.getCustomerId());
                return timeSlotService.bookTimeSlot(timeSlotDtoDto)
                    .doOnNext(timeSlotKafka ->
                        {
                            log.info("Preparing to book a time slot: {}", timeSlotKafka);
                            kafkaService.publishEvent("TimeSlotChosen", timeSlotKafka);
                        }
                    );
            }
        );

    }
}
