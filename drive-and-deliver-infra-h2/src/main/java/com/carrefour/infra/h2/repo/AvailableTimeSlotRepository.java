package com.carrefour.infra.h2.repo;


import com.carrefour.infra.h2.entity.AvailableTimeSlot;
import com.carrefour.infra.h2.entity.Delivery;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface AvailableTimeSlotRepository extends R2dbcRepository<AvailableTimeSlot, Long> {


}
