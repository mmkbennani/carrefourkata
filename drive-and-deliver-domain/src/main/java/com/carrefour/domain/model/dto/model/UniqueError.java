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
 * UniqueError
 */

@JsonTypeName("uniqueError")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UniqueError {

  @JsonProperty("type")
  private String type;

  @JsonProperty("title")
  private String title;

  @JsonProperty("details")
  private String details;

  @JsonProperty("instance")
  private String instance;

  public UniqueError type(String type) {
    this.type = type;
    return this;
  }

}

