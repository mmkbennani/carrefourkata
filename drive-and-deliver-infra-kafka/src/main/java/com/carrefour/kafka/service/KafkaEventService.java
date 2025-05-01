package com.carrefour.kafka.service;

import com.carrefour.domain.model.dto.model.EventDto;
import com.carrefour.domain.repository.KafkaService;
import com.carrefour.kafka.port.EventProducerPort;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Slf4j
public class KafkaEventService implements KafkaService {

    private final EventProducerPort eventProducerPort;

    @Override
    public Mono<Void> publishEvent(String eventType, Object eventData) {
        log.info("Publishing event type: {} with data: {}", eventType, eventData);
        EventDto event = new EventDto(
            UUID.randomUUID(),
            eventType,
            convertToJson(eventData),
            LocalDateTime.now()
        );

        return eventProducerPort.send(event);
    }

    private String convertToJson(Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (JsonProcessingException ex) {
            throw new RuntimeException("Error converting object to JSON", ex);
        }
    }
}
