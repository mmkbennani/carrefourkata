package com.carrefour.infrastructure.h2.repo;

import com.carrefour.infrastructure.h2.entity.TimeSlot;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface TimeSlotRepository extends R2dbcRepository<TimeSlot, Long> {

    Mono<TimeSlot> findTimeSlotBySlotIdAndCustomerId(Long slotId,Long customerId);

}
