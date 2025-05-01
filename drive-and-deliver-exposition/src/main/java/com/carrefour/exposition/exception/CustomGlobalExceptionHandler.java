package com.carrefour.exposition.exception;

import com.carrefour.domain.exception.CarrefourError;
import com.carrefour.domain.exception.CarrefourException;
import com.carrefour.domain.model.dto.model.ErrorReport;
import com.carrefour.exposition.exception.entity.ErrorResponseWebclientType;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;

import static com.carrefour.domain.utils.LoggingUtils.logOnNext;


@Slf4j
@RestControllerAdvice
public class CustomGlobalExceptionHandler {

    protected static final String ERROR_DETAILS_LOG = "-------- Error {}: {}, details: {} --------";
    protected static final String MESSAGE_DETAILS_LOG = "-------- Message : {}, with cause : {} --------";


    @ExceptionHandler(CarrefourException.class)
    public ResponseEntity<CarrefourError> handleCarrefourException(CarrefourException ex) {
        CarrefourError error = ex.getCarrefourError();
        HttpStatus status = HttpStatus.valueOf(Integer.parseInt(error.getHttpStatus()));
        log.error("CarrefourException: status={}, details={}", status, error.getDetails());
        return ResponseEntity
            .status(status)
            .contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .body(error);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorReport> handleRuntimeException(RuntimeException ex) {
        log.error(MESSAGE_DETAILS_LOG, ex.getMessage(), ex.getCause(), ex);

        CarrefourError errorType = new CarrefourError();
        errorType.setHttpStatus("500");
        errorType.setTitle("Bad Request");
        errorType.setDetails(ex.getMessage());
        return ResponseEntity
            .status(500)
            .contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .body(ErrorReport.builder().title(errorType.getTitle()).details(errorType.getDetails()).build());
    }


    @ExceptionHandler(ServerWebInputException.class)
    public Mono<ResponseEntity<ErrorReport>> mismatchedInputException(ServerWebInputException ex) {
        log.error(ex.getMessage(), ex);
        if (ex.getReason() != null && ex.getReason().startsWith("Request body is missing")) {
            CarrefourError errorType = new CarrefourError();
            errorType.setHttpStatus("400");
            errorType.setTitle("Bad Request");
            errorType.setDetails("Required content body is missing..");
            return ErrorResponseWebclientType.buildErrorResponse(errorType)
                .doOnEach(logOnNext(response -> log.error(ERROR_DETAILS_LOG, HttpStatus.BAD_REQUEST, ex.getClass().getSimpleName(), "Required content body is missing..")));

        }
        throw ex;
    }




}
