package com.carrefour.infra.h2.repoimpl;


import com.carrefour.domain.exception.CarrefourError;
import com.carrefour.domain.exception.CarrefourException;
import com.carrefour.domain.model.dto.model.TimeSlotDto;
import com.carrefour.domain.repository.TimeSlotService;
import com.carrefour.infra.h2.entity.AvailableTimeSlot;
import com.carrefour.infra.h2.entity.TimeSlot;
import com.carrefour.infra.h2.repo.AvailableTimeSlotRepository;
import com.carrefour.infra.h2.repo.TimeSlotRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;


@Component
@Data
@AllArgsConstructor
public class TimeSlotServiceImpl implements TimeSlotService {

    private final AvailableTimeSlotRepository availableTimeSlotRepository;
    private final TimeSlotRepository timeSlotRepository;


    @Override
    public Mono<TimeSlotDto> bookTimeSlot(TimeSlotDto timeSlotDto) {
        CarrefourError timeSlotNotFound = CarrefourError.builder()
            .httpStatus("404")
            .details(String.format("Time Slot with ID %s not found", timeSlotDto.getTimeSlotId()))
            .title("Time Slot not found")
            .build();

        CarrefourError reservationAlreadyDone = CarrefourError.builder()
            .httpStatus("409")
            .details(String.format("Customer %s has already done reservation with id slot : %s", timeSlotDto.getCustomerId(), timeSlotDto.getTimeSlotId()))
            .title("Time Slot already reserved")
            .build();

        CarrefourError reservationNotPossible = CarrefourError.builder()
            .httpStatus("403")
            .details(String.format("Reservation not possible for the slot_id : %s", timeSlotDto.getTimeSlotId()))
            .title("Reservation not possible")
            .build();

        return availableTimeSlotRepository.findById(Long.parseLong(String.valueOf(timeSlotDto.getTimeSlotId())))
            .switchIfEmpty(Mono.error(new CarrefourException(timeSlotNotFound)))
            .flatMap(availableSlot -> {
                if (availableSlot.getNumberOfReservation() < availableSlot.getNumberMaxOfReservation()) {
                    // Vérifier si une réservation existe déjà
                    return timeSlotRepository.findTimeSlotBySlotIdAndCustomerId(
                            timeSlotDto.getTimeSlotId(), timeSlotDto.getCustomerId()
                        )
                        .hasElement()
                        .flatMap(hasElement -> {
                            if (hasElement) {
                                // Si une réservation existe déjà
                                return Mono.error(new CarrefourException(reservationAlreadyDone));
                            } else {
                                // Si aucune réservation n'existe, procéder à la sauvegarde
                                return saveTimeSlot(
                                    timeSlotDto,
                                    availableSlot.getStartTime(),
                                    availableSlot.getEndTime(),
                                    availableSlot
                                );
                            }
                        });
                } else {
                    return Mono.error(new CarrefourException(reservationNotPossible));
                }
            });
    }
    private Mono<TimeSlotDto> saveTimeSlot(TimeSlotDto timeSlotDto, String startTime, String endTime, AvailableTimeSlot checkAvailable) {
        TimeSlot build = TimeSlot.builder()
            .slotId(Long.parseLong(String.valueOf(timeSlotDto.getTimeSlotId())))
            .customerId(Long.parseLong(String.valueOf(timeSlotDto.getCustomerId())))
            .startTime(startTime)
            .endTime(endTime)
            .build();
        TimeSlotDto timeSlotDtoSave = TimeSlotDto.builder()
            .timeSlotId(Long.parseLong(String.valueOf(timeSlotDto.getTimeSlotId())))
            .customerId(Long.parseLong(String.valueOf(timeSlotDto.getCustomerId())))
            .startTime(startTime)
            .endTime(endTime)
            .build();
        checkAvailable.setNumberOfReservation(checkAvailable.getNumberOfReservation() + 1);

        return availableTimeSlotRepository.save(checkAvailable)
            .then(timeSlotRepository.save(build))
            .thenReturn(timeSlotDtoSave);
    }
}
