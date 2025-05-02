package com.carrefour.exposition.controller;

import com.carrefour.application.DriveManagement;
import com.carrefour.domain.model.dto.model.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.UUID;


@AllArgsConstructor
@RestController
@Validated
@Slf4j
public class DriveDeliverController {

    private final Logger logger;
    private final DriveManagement driveManagement;
    private static final String TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    @Operation(
        operationId = "bookTimeSlot",
        summary = "SBook a time slot",
        description = "Book a time slot",
        tags = { "Book a time slot" },
        responses = {
            @ApiResponse(responseCode = "200", description = "OK", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class)),
                @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Map.class))
            }),
            @ApiResponse(responseCode = "400", description = "Bad Request. The request is missing a required parameter, includes an unsupported parameter value, repeats a parameter, includes multiple credentials, or is otherwise malformed.", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = BookTimeSlot400Response.class)),
                @Content(mediaType = "application/problem+json", schema = @Schema(implementation = BookTimeSlot400Response.class))
            }),
            @ApiResponse(responseCode = "401", description = "Unauthorized. Authentication of the caller failed."),
            @ApiResponse(responseCode = "403", description = "Forbidden. The caller does not have the access rights required for this operation."),
            @ApiResponse(responseCode = "404", description = "NotFound", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorReport.class)),
                @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ErrorReport.class))
            }),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = ChooseOption500Response.class)),
                @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ChooseOption500Response.class))
            })
        },
        security = {
            @SecurityRequirement(name = "bearerAuth")
        }
    )
    @RequestMapping(
        method = RequestMethod.POST,
        value = "/v1/bookTimeSlot",
        produces = { "application/json", "application/problem+json" },
        consumes = { "application/json" }
    )
    public Mono<ResponseEntity<TimeSlotDto>> bookTimeSlot(
        @NotNull @Parameter(name = "X-Correlation-ID", description = "Identifier value used to monitor the correlation of the requests.", required = true, in = ParameterIn.HEADER) @RequestHeader(value = "X-Correlation-ID", required = true) UUID xCorrelationID,
        @Parameter(name = "TimeSlotDto", description = "chooseOption.") @Valid @RequestBody(required = false) TimeSlotDto timeSlotDto
    ) {
        return driveManagement.bookTimeSlot(Mono.just(timeSlotDto))
            .map(timeSlotResponse ->  ResponseEntity.status(HttpStatus.OK).body(timeSlotResponse));
    }

    @Operation(
        operationId = "chooseOption",
        summary = "SChoose delivery method.",
        description = "Choose delivery method. the available delivery methods are  `DRIVE`, `DELIVERY`, `DELIVERY_TODAY`, `DELIVERY_ASAP`.",
        tags = { "Choose delivery method." },
        responses = {
            @ApiResponse(responseCode = "200", description = "OK", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class)),
                @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Map.class))
            }),
            @ApiResponse(responseCode = "400", description = "Bad Request. The request is missing a required parameter, includes an unsupported parameter value, repeats a parameter, includes multiple credentials, or is otherwise malformed.", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = ChooseOption400Response.class)),
                @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ChooseOption400Response.class))
            }),
            @ApiResponse(responseCode = "401", description = "Unauthorized. Authentication of the caller failed."),
            @ApiResponse(responseCode = "403", description = "Forbidden. The caller does not have the access rights required for this operation."),
            @ApiResponse(responseCode = "404", description = "NotFound", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorReport.class)),
                @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ErrorReport.class))
            }),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = ChooseOption500Response.class)),
                @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ChooseOption500Response.class))
            })
        },
        security = {
            @SecurityRequirement(name = "bearerAuth")
        }
    )
    @RequestMapping(
        method = RequestMethod.POST,
        value = "/v1/chooseOption",
        produces = { "application/json", "application/problem+json" },
        consumes = { "application/json" }
    )
    public Mono<ResponseEntity<DeliveryDto>> chooseOption(

        @NotNull @Parameter(name = "X-Correlation-ID", description = "Identifier value used to monitor the correlation of the requests.", required = true, in = ParameterIn.HEADER) @RequestHeader(value = "X-Correlation-ID", required = true) UUID xCorrelationID,
        @Parameter(name = "ChooseOptionResource", description = "chooseOption.", required = true) @Valid @RequestBody ChooseOptionResource chooseOptionResource
    ) {
        return driveManagement.chooseDeliveryOption(Mono.just(chooseOptionResource))
            .map(chooseOptionResponse ->  ResponseEntity.status(HttpStatus.OK).body(chooseOptionResponse));
    }

}
