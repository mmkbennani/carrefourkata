package com.carrefour.application.orchestration;

import com.carrefour.domain.model.dto.model.ChooseOptionResource;
import com.carrefour.domain.model.dto.model.DeliveryDto;
import com.carrefour.domain.model.dto.model.TimeSlotDto;
import reactor.core.publisher.Mono;

public interface DriveManagement {

    Mono<DeliveryDto> chooseDeliveryOption(Mono<ChooseOptionResource> deliveryOption);
    Mono<TimeSlotDto> bookTimeSlot(Mono<TimeSlotDto> timeSlotDtoMono);
}
