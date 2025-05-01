package com.carrefour.exposition.exception;

import com.carrefour.domain.exception.CarrefourError;
import com.carrefour.domain.exception.CarrefourException;
import com.carrefour.domain.model.dto.model.ErrorReport;
import com.carrefour.exposition.exception.entity.ErrorResponseWebclientType;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;

import static com.carrefour.domain.utils.LoggingUtils.logOnNext;


@Slf4j
public abstract class CustomGlobalExceptionHandler {

    protected static final String ERROR_DETAILS_LOG = "-------- Error {}: {}, details: {} --------";
    protected static final String MESSAGE_DETAILS_LOG = "-------- Message : {}, with cause : {} --------";


    protected abstract Logger getLogger();

    @ExceptionHandler(CarrefourException.class)
    public Mono<ResponseEntity<ErrorReport>> handleWebclientException(CarrefourException ex) {
        getLogger().error(MESSAGE_DETAILS_LOG, ex.getMessage(), ex.getCause(), ex);
        return ErrorResponseWebclientType.buildErrorResponse(ex.getCarrefourError());
    }



    @ExceptionHandler(RuntimeException.class)
    public Mono<ResponseEntity<ErrorReport>> handleRuntimeException(RuntimeException ex) {
        getLogger().error(MESSAGE_DETAILS_LOG, ex.getMessage(), ex.getCause(), ex);

        CarrefourError errorType = new CarrefourError();
        errorType.setHttpStatus("500");
        errorType.setTitle("Bad Request");
        errorType.setDetails(ex.getMessage());
        return ErrorResponseWebclientType.buildErrorResponse(errorType)
            .doOnEach(logOnNext(response -> getLogger().error(ERROR_DETAILS_LOG, HttpStatus.BAD_REQUEST, ex.getClass().getSimpleName(), ex.getMessage())));
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
                .doOnEach(logOnNext(response -> getLogger().error(ERROR_DETAILS_LOG, HttpStatus.BAD_REQUEST, ex.getClass().getSimpleName(), "Required content body is missing..")));

        }
        throw ex;
    }




}
