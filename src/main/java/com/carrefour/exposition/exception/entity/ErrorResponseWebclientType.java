package com.carrefour.exposition.exception.entity;

import com.carrefour.domain.exception.CarrefourError;
import com.carrefour.domain.model.dto.model.ErrorReport;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

public class ErrorResponseWebclientType {

    public static Mono<ResponseEntity<ErrorReport>> buildErrorResponse(CarrefourError errorType) {
        return Mono.just(
                ErrorReport.builder()
                .type(errorType.getType())
                .title(errorType.getTitle())
                .details(errorType.getDetails())
                .instance(errorType.getInstance())
                .build()
            ).map(error -> ResponseEntity.status(Integer.parseInt(errorType.getHttpStatus())).body(error));
    }

}
