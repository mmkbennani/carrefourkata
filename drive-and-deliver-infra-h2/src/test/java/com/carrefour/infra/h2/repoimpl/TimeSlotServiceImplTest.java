package com.carrefour.infra.h2.repoimpl;

import com.carrefour.domain.exception.CarrefourException;
import com.carrefour.domain.model.dto.model.TimeSlotDto;
import com.carrefour.infra.h2.entity.AvailableTimeSlot;
import com.carrefour.infra.h2.entity.TimeSlot;
import com.carrefour.infra.h2.repo.AvailableTimeSlotRepository;
import com.carrefour.infra.h2.repo.TimeSlotRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TimeSlotServiceImplTest {

    @Mock
    private AvailableTimeSlotRepository availableTimeSlotRepository;

    @Mock
    private TimeSlotRepository timeSlotRepository;

    @InjectMocks
    private TimeSlotServiceImpl timeSlotService;

    private TimeSlotDto timeSlotDto;
    private AvailableTimeSlot availableTimeSlot;
    private TimeSlot timeSlot;

    @BeforeEach
    void setUp() {
        // Initialiser timeSlotDto
        timeSlotDto = TimeSlotDto.builder()
            .timeSlotId(101L)
            .customerId(1L)
            .startTime("10:00")
            .endTime("11:00")
            .build();

        // Initialiser availableTimeSlot
        availableTimeSlot = AvailableTimeSlot.builder()
            .id(101L)
            .startTime("10:00")
            .endTime("11:00")
            .numberOfReservation(0)
            .numberMaxOfReservation(10)
            .build();

        // Initialiser timeSlot
        timeSlot = TimeSlot.builder()
            .id(1L)
            .slotId(101L)
            .customerId(1L)
            .startTime("10:00")
            .endTime("11:00")
            .build();

        // Suppression des stubs par défaut pour éviter UnnecessaryStubbingException
    }

    @Test
    void bookTimeSlot_ShouldBookSuccessfully_WhenSlotIsAvailableAndNotAlreadyBooked() {
        // Arrange
        when(availableTimeSlotRepository.findById(101L)).thenReturn(Mono.just(availableTimeSlot));
        when(timeSlotRepository.findTimeSlotBySlotIdAndCustomerId(101L, 1L)).thenReturn(Mono.empty());
        when(availableTimeSlotRepository.save(any())).thenReturn(Mono.just(availableTimeSlot));
        when(timeSlotRepository.save(any())).thenReturn(Mono.just(timeSlot));

        // Act
        Mono<TimeSlotDto> result = timeSlotService.bookTimeSlot(timeSlotDto);

        // Assert
        StepVerifier.create(result)
            .expectNextMatches(dto ->
                dto.getTimeSlotId() == 101L &&
                    dto.getCustomerId() == 1L &&
                    "10:00".equals(dto.getStartTime()) &&
                    "11:00".equals(dto.getEndTime()))
            .verifyComplete();

        verify(availableTimeSlotRepository).findById(101L);
        verify(timeSlotRepository).findTimeSlotBySlotIdAndCustomerId(101L, 1L);
        verify(availableTimeSlotRepository).save(any(AvailableTimeSlot.class));
        verify(timeSlotRepository).save(any(TimeSlot.class));
    }

    @Test
    void bookTimeSlot_ShouldReturnError_WhenSlotDoesNotExist() {
        // Arrange
        when(availableTimeSlotRepository.findById(101L)).thenReturn(Mono.empty());

        // Act
        Mono<TimeSlotDto> result = timeSlotService.bookTimeSlot(timeSlotDto);

        // Assert
        StepVerifier.create(result)
            .expectErrorMatches(e ->
                e instanceof CarrefourException &&
                    "Time Slot with ID 101 not found".equals(((CarrefourException) e).getCarrefourError().getDetails()) &&
                    "404".equals(((CarrefourException) e).getCarrefourError().getHttpStatus()))
            .verify();

        verify(availableTimeSlotRepository).findById(101L);
        verify(timeSlotRepository, never()).findTimeSlotBySlotIdAndCustomerId(anyLong(), anyLong());
        verify(availableTimeSlotRepository, never()).save(any(AvailableTimeSlot.class));
        verify(timeSlotRepository, never()).save(any(TimeSlot.class));
    }

    @Test
    void bookTimeSlot_ShouldReturnError_WhenSlotAlreadyReservedByCustomer() {
        // Arrange
        when(availableTimeSlotRepository.findById(101L)).thenReturn(Mono.just(availableTimeSlot));
        when(timeSlotRepository.findTimeSlotBySlotIdAndCustomerId(101L, 1L)).thenReturn(Mono.just(timeSlot));

        // Act
        Mono<TimeSlotDto> result = timeSlotService.bookTimeSlot(timeSlotDto);

        // Assert
        StepVerifier.create(result)
            .expectErrorMatches(e ->
                e instanceof CarrefourException &&
                    "Customer 1 has already done reservation with id slot : 101".equals(((CarrefourException) e).getCarrefourError().getDetails()) &&
                    "409".equals(((CarrefourException) e).getCarrefourError().getHttpStatus()))
            .verify();

        verify(availableTimeSlotRepository).findById(101L);
        verify(timeSlotRepository).findTimeSlotBySlotIdAndCustomerId(101L, 1L);
        verify(availableTimeSlotRepository, never()).save(any(AvailableTimeSlot.class));
        verify(timeSlotRepository, never()).save(any(TimeSlot.class));
    }

    @Test
    void bookTimeSlot_ShouldReturnError_WhenSlotIsFullyBooked() {
        // Arrange
        AvailableTimeSlot fullSlot = AvailableTimeSlot.builder()
            .id(101L)
            .startTime("10:00")
            .endTime("11:00")
            .numberOfReservation(10)
            .numberMaxOfReservation(10)
            .build();

        when(availableTimeSlotRepository.findById(101L)).thenReturn(Mono.just(fullSlot));

        // Act
        Mono<TimeSlotDto> result = timeSlotService.bookTimeSlot(timeSlotDto);

        // Assert
        StepVerifier.create(result)
            .expectErrorMatches(e ->
                e instanceof CarrefourException &&
                    "Reservation not possible for the slot_id : 101".equals(((CarrefourException) e).getCarrefourError().getDetails()) &&
                    "403".equals(((CarrefourException) e).getCarrefourError().getHttpStatus()))
            .verify();

        verify(availableTimeSlotRepository).findById(101L);
        verify(timeSlotRepository, never()).findTimeSlotBySlotIdAndCustomerId(anyLong(), anyLong());
        verify(availableTimeSlotRepository, never()).save(any(AvailableTimeSlot.class));
        verify(timeSlotRepository, never()).save(any(TimeSlot.class));
    }

    @Test
    void bookTimeSlot_ShouldPropagateError_WhenFindAvailableTimeSlotThrowsError() {
        // Arrange
        RuntimeException exception = new RuntimeException("Database error");
        when(availableTimeSlotRepository.findById(101L)).thenReturn(Mono.error(exception));

        // Act
        Mono<TimeSlotDto> result = timeSlotService.bookTimeSlot(timeSlotDto);

        // Assert
        StepVerifier.create(result)
            .expectErrorMatches(e -> e.equals(exception))
            .verify();

        verify(availableTimeSlotRepository).findById(101L);
        verify(timeSlotRepository, never()).findTimeSlotBySlotIdAndCustomerId(anyLong(), anyLong());
        verify(availableTimeSlotRepository, never()).save(any(AvailableTimeSlot.class));
        verify(timeSlotRepository, never()).save(any(TimeSlot.class));
    }

    @Test
    void bookTimeSlot_ShouldPropagateError_WhenFindTimeSlotThrowsError() {
        // Arrange
        RuntimeException exception = new RuntimeException("Database error");
        when(availableTimeSlotRepository.findById(101L)).thenReturn(Mono.just(availableTimeSlot));
        when(timeSlotRepository.findTimeSlotBySlotIdAndCustomerId(101L, 1L)).thenReturn(Mono.error(exception));

        // Act
        Mono<TimeSlotDto> result = timeSlotService.bookTimeSlot(timeSlotDto);

        // Assert
        StepVerifier.create(result)
            .expectErrorMatches(e -> e.equals(exception))
            .verify();

        verify(availableTimeSlotRepository).findById(101L);
        verify(timeSlotRepository).findTimeSlotBySlotIdAndCustomerId(101L, 1L);
        verify(availableTimeSlotRepository, never()).save(any(AvailableTimeSlot.class));
        verify(timeSlotRepository, never()).save(any(TimeSlot.class));
    }

    @Test
    void bookTimeSlot_ShouldPropagateError_WhenSaveAvailableTimeSlotThrowsError() {
        // Arrange
        RuntimeException exception = new RuntimeException("Database error on save");
        when(availableTimeSlotRepository.findById(101L)).thenReturn(Mono.just(availableTimeSlot));
        when(timeSlotRepository.findTimeSlotBySlotIdAndCustomerId(101L, 1L)).thenReturn(Mono.empty());
        when(availableTimeSlotRepository.save(any(AvailableTimeSlot.class))).thenReturn(Mono.error(exception));

        // Important : Il faut également mocker timeSlotRepository.save car il est appelé dans then()
        when(timeSlotRepository.save(any(TimeSlot.class))).thenReturn(Mono.just(timeSlot));

        // Act
        Mono<TimeSlotDto> result = timeSlotService.bookTimeSlot(timeSlotDto);

        // Assert
        StepVerifier.create(result)
            .expectErrorMatches(e -> e.equals(exception))
            .verify();

        verify(availableTimeSlotRepository).findById(101L);
        verify(timeSlotRepository).findTimeSlotBySlotIdAndCustomerId(101L, 1L);
        verify(availableTimeSlotRepository).save(any(AvailableTimeSlot.class));
        // Ne pas vérifier que save n'est jamais appelé, car il est appelé dans la chaîne then()
    }

    @Test
    void bookTimeSlot_ShouldPropagateError_WhenSaveTimeSlotThrowsError() {
        // Arrange
        RuntimeException exception = new RuntimeException("Database error on save time slot");
        when(availableTimeSlotRepository.findById(101L)).thenReturn(Mono.just(availableTimeSlot));
        when(timeSlotRepository.findTimeSlotBySlotIdAndCustomerId(101L, 1L)).thenReturn(Mono.empty());
        when(availableTimeSlotRepository.save(any(AvailableTimeSlot.class))).thenReturn(Mono.just(availableTimeSlot));
        when(timeSlotRepository.save(any(TimeSlot.class))).thenReturn(Mono.error(exception));

        // Act
        Mono<TimeSlotDto> result = timeSlotService.bookTimeSlot(timeSlotDto);

        // Assert
        StepVerifier.create(result)
            .expectErrorMatches(e -> e.equals(exception))
            .verify();

        verify(availableTimeSlotRepository).findById(101L);
        verify(timeSlotRepository).findTimeSlotBySlotIdAndCustomerId(101L, 1L);
        verify(availableTimeSlotRepository).save(any(AvailableTimeSlot.class));
        verify(timeSlotRepository).save(any(TimeSlot.class));
    }

    @Test
    void bookTimeSlot_ShouldIncrementNumberOfReservation_WhenSlotIsBooked() {
        // Arrange
        when(availableTimeSlotRepository.findById(101L)).thenReturn(Mono.just(availableTimeSlot));
        when(timeSlotRepository.findTimeSlotBySlotIdAndCustomerId(101L, 1L)).thenReturn(Mono.empty());
        when(availableTimeSlotRepository.save(any())).thenReturn(Mono.just(availableTimeSlot));
        when(timeSlotRepository.save(any())).thenReturn(Mono.just(timeSlot));

        // Act
        timeSlotService.bookTimeSlot(timeSlotDto).block();

        // Assert
        verify(availableTimeSlotRepository).save(argThat(slot ->
            slot.getNumberOfReservation() == 1
        ));
    }
}
