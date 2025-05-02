package com.carrefour.infrastructure.h2.repo;


import com.carrefour.infrastructure.h2.entity.AvailableTimeSlot;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface AvailableTimeSlotRepository extends R2dbcRepository<AvailableTimeSlot, Long> {


}
