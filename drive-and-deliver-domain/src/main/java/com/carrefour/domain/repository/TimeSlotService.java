package com.carrefour.domain.repository;

import com.carrefour.domain.model.dto.model.TimeSlotDto;
import reactor.core.publisher.Mono;


public interface TimeSlotService {

    Mono<TimeSlotDto> bookTimeSlot(TimeSlotDto timeSlotDto);
}
