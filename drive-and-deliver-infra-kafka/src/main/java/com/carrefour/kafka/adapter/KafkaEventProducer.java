package com.carrefour.kafka.adapter;

import com.carrefour.domain.model.dto.model.EventDto;
import com.carrefour.kafka.port.EventProducerPort;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Component
public class KafkaEventProducer implements EventProducerPort {

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${kafka.topic.drive-deliver}")
    private String topicName;

    @Override
    public Mono<Void> send(EventDto eventDto) {
        return Mono.fromRunnable(() -> {
            try {
                String json = new ObjectMapper().writeValueAsString(eventDto);
                kafkaTemplate.send(topicName, json);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Failed to serialize event", e);
            }
        }).then();
    }
}
