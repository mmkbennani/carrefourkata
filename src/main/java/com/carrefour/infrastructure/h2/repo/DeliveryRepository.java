package com.carrefour.infrastructure.h2.repo;


import com.carrefour.infrastructure.h2.entity.Delivery;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface DeliveryRepository extends R2dbcRepository<Delivery, Long> {

  Mono<Delivery> findByDeliveryOptionAndCustomerId(String deliveryOption, Long customerId);

}
