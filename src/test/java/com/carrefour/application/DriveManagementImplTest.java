package com.carrefour.application;

import com.carrefour.domain.model.dto.model.ChooseOptionResource;
import com.carrefour.domain.model.dto.model.DeliveryDto;
import com.carrefour.domain.model.dto.model.DeliveryOptionEnum;
import com.carrefour.domain.model.dto.model.TimeSlotDto;
import com.carrefour.domain.repository.DeliveryService;
import com.carrefour.domain.repository.KafkaService;
import com.carrefour.domain.repository.TimeSlotService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DriveManagementImplTest {

    @Mock
    private DeliveryService deliveryService;

    @Mock
    private TimeSlotService timeSlotService;

    @Mock
    private KafkaService kafkaService;

    @InjectMocks
    private DriveManagementImpl driveManagement;

    private ChooseOptionResource chooseOptionResource;
    private DeliveryDto deliveryDto;
    private TimeSlotDto timeSlotDto;

    @BeforeEach
    void setUp() {
        // Initialiser les objets pour les tests
        chooseOptionResource = new ChooseOptionResource();
        chooseOptionResource.setCustomerId(BigDecimal.valueOf(1L));
        chooseOptionResource.setDeliveryOption(com.carrefour.domain.model.dto.model.ChooseOptionResource.DeliveryOptionEnum.DRIVE);

        deliveryDto = new DeliveryDto();
        deliveryDto.setCustomerId(1L);
        deliveryDto.setDeliveryOption(DeliveryOptionEnum.DRIVE);

        timeSlotDto = new TimeSlotDto();
        timeSlotDto.setCustomerId(1L);
        timeSlotDto.setTimeSlotId(101L);
        timeSlotDto.setStartTime("10:00");
        timeSlotDto.setEndTime("11:00");
    }

    @Test
    void chooseDeliveryOption_ShouldReturnDeliveryDto_WhenValidOptionProvided() {
        // Arrange
        when(deliveryService.saveDelivery(eq(DeliveryOptionEnum.DRIVE), eq(BigDecimal.valueOf(1L))))
                .thenReturn(Mono.just(deliveryDto));

        // Au lieu de doNothing(), utilisez when().thenReturn() pour les méthodes non-void
        when(kafkaService.publishEvent(anyString(), any())).thenReturn(null);

        // Act
        Mono<DeliveryDto> result = driveManagement.chooseDeliveryOption(Mono.just(chooseOptionResource));

        // Assert
        StepVerifier.create(result)
                .expectNext(deliveryDto)
                .verifyComplete();

        verify(deliveryService, times(1)).saveDelivery(eq(DeliveryOptionEnum.DRIVE), eq(BigDecimal.valueOf(1L)));
        verify(kafkaService, times(1)).publishEvent(eq("DeliveryOptionChosen"), eq(deliveryDto));
    }

    @Test
    void chooseDeliveryOption_ShouldPropagateError_WhenDeliveryServiceFails() {
        // Arrange
        RuntimeException exception = new RuntimeException("Service failure");
        when(deliveryService.saveDelivery(eq(DeliveryOptionEnum.DRIVE), eq(BigDecimal.valueOf(1L))))
                .thenReturn(Mono.error(exception));

        // Act
        Mono<DeliveryDto> result = driveManagement.chooseDeliveryOption(Mono.just(chooseOptionResource));

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(e -> e.equals(exception))
                .verify();

        verify(deliveryService, times(1)).saveDelivery(eq(DeliveryOptionEnum.DRIVE), eq(BigDecimal.valueOf(1L)));
        verify(kafkaService, times(0)).publishEvent(anyString(), any());
    }

    @Test
    void bookTimeSlot_ShouldReturnTimeSlotDto_WhenValidSlotProvided() {
        // Arrange
        when(timeSlotService.bookTimeSlot(eq(timeSlotDto)))
                .thenReturn(Mono.just(timeSlotDto));

        // Au lieu de doNothing(), utilisez when().thenReturn() pour les méthodes non-void
        when(kafkaService.publishEvent(anyString(), any())).thenReturn(null);

        // Act
        Mono<TimeSlotDto> result = driveManagement.bookTimeSlot(Mono.just(timeSlotDto));

        // Assert
        StepVerifier.create(result)
                .expectNext(timeSlotDto)
                .verifyComplete();

        verify(timeSlotService, times(1)).bookTimeSlot(eq(timeSlotDto));
        verify(kafkaService, times(1)).publishEvent(eq("TimeSlotChosen"), eq(timeSlotDto));
    }

    @Test
    void bookTimeSlot_ShouldPropagateError_WhenTimeSlotServiceFails() {
        // Arrange
        RuntimeException exception = new RuntimeException("Service failure");
        when(timeSlotService.bookTimeSlot(eq(timeSlotDto)))
                .thenReturn(Mono.error(exception));

        // Act
        Mono<TimeSlotDto> result = driveManagement.bookTimeSlot(Mono.just(timeSlotDto));

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(e -> e.equals(exception))
                .verify();

        verify(timeSlotService, times(1)).bookTimeSlot(eq(timeSlotDto));
        verify(kafkaService, times(0)).publishEvent(anyString(), any());
    }

    @Test
    void bookTimeSlot_ShouldHandleKafkaFailure_AndStillReturnTimeSlotDto() {
        // Arrange
        when(timeSlotService.bookTimeSlot(eq(timeSlotDto)))
                .thenReturn(Mono.just(timeSlotDto));

        // Simuler une erreur dans le service Kafka
        RuntimeException kafkaException = new RuntimeException("Kafka error");
        when(kafkaService.publishEvent(anyString(), any())).thenThrow(kafkaException);

        // Act
        Mono<TimeSlotDto> result = driveManagement.bookTimeSlot(Mono.just(timeSlotDto));

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(e -> e.equals(kafkaException))
                .verify();

        verify(timeSlotService, times(1)).bookTimeSlot(eq(timeSlotDto));
        verify(kafkaService, times(1)).publishEvent(eq("TimeSlotChosen"), eq(timeSlotDto));
    }

    @Test
    void chooseDeliveryOption_ShouldHandleEmptyMono() {
        // Arrange
        Mono<ChooseOptionResource> emptyMono = Mono.empty();

        // Act
        Mono<DeliveryDto> result = driveManagement.chooseDeliveryOption(emptyMono);

        // Assert
        StepVerifier.create(result)
                .verifyComplete();

        verify(deliveryService, times(0)).saveDelivery(any(), any());
        verify(kafkaService, times(0)).publishEvent(anyString(), any());
    }

    @Test
    void bookTimeSlot_ShouldHandleEmptyMono() {
        // Arrange
        Mono<TimeSlotDto> emptyMono = Mono.empty();

        // Act
        Mono<TimeSlotDto> result = driveManagement.bookTimeSlot(emptyMono);

        // Assert
        StepVerifier.create(result)
                .verifyComplete();

        verify(timeSlotService, times(0)).bookTimeSlot(any());
        verify(kafkaService, times(0)).publishEvent(anyString(), any());
    }
}
