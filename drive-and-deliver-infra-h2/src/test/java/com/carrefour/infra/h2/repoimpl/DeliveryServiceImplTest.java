package com.carrefour.infra.h2.repoimpl;

import com.carrefour.domain.model.dto.model.DeliveryDto;
import com.carrefour.domain.model.dto.model.DeliveryOptionEnum;
import com.carrefour.infra.h2.entity.Delivery;
import com.carrefour.infra.h2.mapper.DeliveryMapper;
import com.carrefour.infra.h2.repo.DeliveryRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeliveryServiceImplTest {

    @Mock
    private DeliveryRepository deliveryRepository;

    @Mock
    private DeliveryMapper deliveryMapper;

    @InjectMocks
    private DeliveryServiceImpl deliveryService;

    private DeliveryDto deliveryDto;
    private Delivery delivery;
    private BigDecimal customerId;
    private DeliveryOptionEnum deliveryOption;

    @BeforeEach
    void setUp() {
        customerId = BigDecimal.valueOf(1L);
        deliveryOption = DeliveryOptionEnum.DRIVE;

        // Initialiser deliveryDto
        deliveryDto = DeliveryDto.builder()
            .id(1L)
            .customerId(customerId.longValue())
            .deliveryOption(deliveryOption)
            .build();

        // Initialiser delivery
        delivery = Delivery.builder()
            .id(1L)
            .customerId(customerId.longValue())
            .deliveryOption(com.carrefour.infra.h2.entity.DeliveryOptionEnum.DRIVE)
            .build();
    }

    @Test
    void saveDelivery_ShouldReturnExistingDelivery_WhenDeliveryExists() {
        // Arrange
        when(deliveryRepository.findByDeliveryOptionAndCustomerId(
            deliveryOption.getValue(), customerId.longValue()))
            .thenReturn(Mono.just(delivery));
        when(deliveryMapper.deliveryToDeliveryDto(delivery)).thenReturn(deliveryDto);

        when(deliveryRepository.save(any(Delivery.class)))
            .thenReturn(Mono.just(delivery));

        // Act
        Mono<DeliveryDto> result = deliveryService.saveDelivery(deliveryOption, customerId);

        // Assert
        StepVerifier.create(result)
            .expectNext(deliveryDto)
            .verifyComplete();

        verify(deliveryRepository).findByDeliveryOptionAndCustomerId(
            deliveryOption.getValue(), customerId.longValue());
        verify(deliveryMapper).deliveryToDeliveryDto(delivery);
    }

    @Test
    void saveDelivery_ShouldCreateNewDelivery_WhenDeliveryDoesNotExist() {
        // Arrange
        when(deliveryRepository.findByDeliveryOptionAndCustomerId(
            deliveryOption.getValue(), customerId.longValue()))
            .thenReturn(Mono.empty());
        when(deliveryRepository.save(any(Delivery.class))).thenReturn(Mono.just(delivery));

        // Act
        Mono<DeliveryDto> result = deliveryService.saveDelivery(deliveryOption, customerId);

        // Assert
        StepVerifier.create(result)
            .expectNextMatches(dto ->
                dto.getId() == 1L &&
                    dto.getCustomerId() == customerId.longValue() &&
                    dto.getDeliveryOption() == deliveryOption)
            .verifyComplete();

        verify(deliveryRepository).findByDeliveryOptionAndCustomerId(
            deliveryOption.getValue(), customerId.longValue());
        verify(deliveryRepository).save(any(Delivery.class));
        verify(deliveryMapper, never()).deliveryToDeliveryDto(any(Delivery.class));
    }

    @Test
    void saveDelivery_ShouldPropagateError_WhenRepositoryThrowsError() {
        // Arrange
        RuntimeException exception = new RuntimeException("Database error");
        when(deliveryRepository.findByDeliveryOptionAndCustomerId(
            deliveryOption.getValue(), customerId.longValue()))
            .thenReturn(Mono.error(exception));


        when(deliveryRepository.save(any(Delivery.class)))
            .thenReturn(Mono.just(delivery));
        // Act
        Mono<DeliveryDto> result = deliveryService.saveDelivery(deliveryOption, customerId);

        // Assert
        StepVerifier.create(result)
            .expectErrorMatches(e -> e.equals(exception))
            .verify();

        verify(deliveryRepository).findByDeliveryOptionAndCustomerId(
            deliveryOption.getValue(), customerId.longValue());
    }

    @Test
    void saveDelivery_ShouldPropagateError_WhenSaveRepositoryThrowsError() {
        // Arrange
        RuntimeException exception = new RuntimeException("Database error on save");
        when(deliveryRepository.findByDeliveryOptionAndCustomerId(
            deliveryOption.getValue(), customerId.longValue()))
            .thenReturn(Mono.empty());
        when(deliveryRepository.save(any(Delivery.class)))
            .thenReturn(Mono.error(exception));

        // Act
        Mono<DeliveryDto> result = deliveryService.saveDelivery(deliveryOption, customerId);

        // Assert
        StepVerifier.create(result)
            .expectErrorMatches(e -> e.equals(exception))
            .verify();

        verify(deliveryRepository).findByDeliveryOptionAndCustomerId(
            deliveryOption.getValue(), customerId.longValue());
        verify(deliveryRepository).save(any(Delivery.class));
        verify(deliveryMapper, never()).deliveryToDeliveryDto(any(Delivery.class));
    }

    @Test
    void saveDelivery_ShouldHandleOtherDeliveryOptions() {
        // Arrange
        DeliveryOptionEnum otherOption = DeliveryOptionEnum.DELIVERY;
        com.carrefour.infra.h2.entity.DeliveryOptionEnum otherEntityOption =
            com.carrefour.infra.h2.entity.DeliveryOptionEnum.DELIVERY;

        Delivery otherDelivery = Delivery.builder()
            .id(2L)
            .customerId(customerId.longValue())
            .deliveryOption(otherEntityOption)
            .build();

        when(deliveryRepository.findByDeliveryOptionAndCustomerId(
            otherOption.getValue(), customerId.longValue()))
            .thenReturn(Mono.empty());
        when(deliveryRepository.save(any(Delivery.class))).thenReturn(Mono.just(otherDelivery));

        // Act
        Mono<DeliveryDto> result = deliveryService.saveDelivery(otherOption, customerId);

        // Assert
        StepVerifier.create(result)
            .expectNextMatches(dto ->
                dto.getId() == 2L &&
                    dto.getCustomerId() == customerId.longValue() &&
                    dto.getDeliveryOption() == otherOption)
            .verifyComplete();

        verify(deliveryRepository).findByDeliveryOptionAndCustomerId(
            otherOption.getValue(), customerId.longValue());
        verify(deliveryRepository).save(any(Delivery.class));
    }
}
