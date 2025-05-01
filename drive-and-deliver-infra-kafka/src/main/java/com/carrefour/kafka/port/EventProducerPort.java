package com.carrefour.kafka.port;

import com.carrefour.domain.model.dto.model.EventDto;
import reactor.core.publisher.Mono;

public interface EventProducerPort {
    Mono<Void> send(EventDto eventDto);
}
