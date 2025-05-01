package com.carrefour.exposition.cucumber;

import com.carrefour.application.orchestration.DriveManagement;
import com.carrefour.domain.exception.CarrefourError;
import com.carrefour.domain.exception.CarrefourException;
import com.carrefour.domain.model.dto.model.ChooseOptionResource;
import com.carrefour.domain.model.dto.model.DeliveryDto;
import com.carrefour.domain.model.dto.model.DeliveryOptionEnum;
import com.carrefour.domain.model.dto.model.TimeSlotDto;
import com.carrefour.exposition.controller.DriveDeliverController;
import com.carrefour.exposition.exception.CustomGlobalExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = DriveDeliverController.class)
@ContextConfiguration(classes = {DriveDeliverControllerIntegrationTest.TestConfig.class})
public class DriveDeliverControllerIntegrationTest {

    @Configuration
    static class TestConfig {
        @Bean
        public DriveDeliverController driveDeliverController(DriveManagement driveManagement) {
            // Fournir un Logger réel pour le contrôleur
            Logger logger = LoggerFactory.getLogger(DriveDeliverController.class);
            return new DriveDeliverController(logger, driveManagement);
        }

        @Bean
        public CustomGlobalExceptionHandler customGlobalExceptionHandler() {
            return new CustomGlobalExceptionHandler() {
                @Override
                protected Logger getLogger() {
                    return LoggerFactory.getLogger(CustomGlobalExceptionHandler.class);
                }
            };
        }
    }

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private DriveManagement driveManagement;

    private ObjectMapper objectMapper;
    private UUID correlationId;
    private ChooseOptionResource chooseOptionResource;
    private DeliveryDto deliveryDto;
    private TimeSlotDto timeSlotDto;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        correlationId = UUID.randomUUID();

        // Initialiser chooseOptionResource
        chooseOptionResource = new ChooseOptionResource();
        chooseOptionResource.setCustomerId(BigDecimal.valueOf(1L));
        chooseOptionResource.setDeliveryOption(ChooseOptionResource.DeliveryOptionEnum.DRIVE);

        // Initialiser deliveryDto
        deliveryDto = new DeliveryDto();
        deliveryDto.setCustomerId(1L);
        deliveryDto.setDeliveryOption(DeliveryOptionEnum.DRIVE);

        // Initialiser timeSlotDto
        timeSlotDto = new TimeSlotDto();
        timeSlotDto.setCustomerId(1L);
        timeSlotDto.setTimeSlotId(101L);
        timeSlotDto.setStartTime("10:00");
        timeSlotDto.setEndTime("11:00");
    }

    @Test
    void chooseOption_ShouldReturnDeliveryDto_WhenSuccessful() {
        // Arrange
        when(driveManagement.chooseDeliveryOption(any())).thenReturn(Mono.just(deliveryDto));

        // Act & Assert
        webTestClient.post()
            .uri("/v1/chooseOption")
            .header("X-Correlation-ID", correlationId.toString())
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(chooseOptionResource))
            .exchange()
            .expectStatus().isOk()
            .expectBody(DeliveryDto.class)
            .isEqualTo(deliveryDto);
    }

    @Test
    void chooseOption_ShouldReturnError_WhenServiceFails() {
        // Arrange
        CarrefourError error = CarrefourError.builder()
            .httpStatus("500")
            .title("Internal Server Error")
            .details("Service failure")
            .build();

        when(driveManagement.chooseDeliveryOption(any()))
            .thenReturn(Mono.error(new CarrefourException(error)));

        // Act & Assert
        webTestClient.post()
            .uri("/v1/chooseOption")
            .header("X-Correlation-ID", correlationId.toString())
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(chooseOptionResource))
            .exchange()
            .expectStatus().is5xxServerError();
    }

    @Test
    void bookTimeSlot_ShouldReturnTimeSlotDto_WhenSuccessful() {
        // Arrange
        when(driveManagement.bookTimeSlot(any())).thenReturn(Mono.just(timeSlotDto));

        // Act & Assert
        webTestClient.post()
            .uri("/v1/bookTimeSlot")
            .header("X-Correlation-ID", correlationId.toString())
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(timeSlotDto))
            .exchange()
            .expectStatus().isOk()
            .expectBody(TimeSlotDto.class)
            .isEqualTo(timeSlotDto);
    }

    @Test
    void bookTimeSlot_ShouldReturnError_WhenSlotNotFound() {
        // Arrange
        CarrefourError error = CarrefourError.builder()
            .httpStatus("404")
            .title("Time Slot not found")
            .details("Time Slot with ID 101 not found")
            .build();

        when(driveManagement.bookTimeSlot(any()))
            .thenReturn(Mono.error(new CarrefourException(error)));

        // Act & Assert
        webTestClient.post()
            .uri("/v1/bookTimeSlot")
            .header("X-Correlation-ID", correlationId.toString())
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(timeSlotDto))
            .exchange()
            .expectStatus().isNotFound();
    }

    @Test
    void bookTimeSlot_ShouldReturnError_WhenSlotAlreadyReserved() {
        // Arrange
        CarrefourError error = CarrefourError.builder()
            .httpStatus("409")
            .title("Time Slot already reserved")
            .details("Customer 1 has already done reservation with id slot : 101")
            .build();

        when(driveManagement.bookTimeSlot(any()))
            .thenReturn(Mono.error(new CarrefourException(error)));

        // Act & Assert
        webTestClient.post()
            .uri("/v1/bookTimeSlot")
            .header("X-Correlation-ID", correlationId.toString())
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(timeSlotDto))
            .exchange()
            .expectStatus().isEqualTo(409);
    }
}
