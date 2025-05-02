package com.carrefour.domain.model.dto.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

/**
 * ChooseOption500Response
 */

@JsonTypeName("chooseOption_500_response")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChooseOption500Response {

  @JsonProperty("title")
  private String title;

  @JsonProperty("details")
  private String details;

  @JsonProperty("type")
  private String type;

  public ChooseOption500Response title(String title) {
    this.title = title;
    return this;
  }

}

