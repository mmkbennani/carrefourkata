package com.carrefour.domain.repository;

import com.carrefour.domain.model.dto.model.TimeSlotDto;
import reactor.core.publisher.Mono;


public interface KafkaService {

    Mono<Void> publishEvent(String eventType, Object eventData);
}
