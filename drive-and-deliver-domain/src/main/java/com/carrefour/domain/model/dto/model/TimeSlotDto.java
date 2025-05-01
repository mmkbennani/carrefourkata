package com.carrefour.domain.model.dto.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * TimeSlotDto
 */

@JsonTypeName("timeSlot")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TimeSlotDto {

  @JsonProperty("startTime")
  private String startTime;

  @JsonProperty("endTime")
  private String endTime;

  @JsonProperty("timeSlotId")
  private Long timeSlotId;

  @JsonProperty("customerId")
  private Long customerId;

}

