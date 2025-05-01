package com.carrefour.domain.model.dto.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * ErrorReport
 */

@JsonTypeName("errorReport")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ErrorReport {

  @JsonProperty("errors")
  @Valid
  private List<UniqueError> errors = null;

  @JsonProperty("type")
  private String type;

  @JsonProperty("title")
  private String title;

  @JsonProperty("details")
  private String details;

  @JsonProperty("instance")
  private String instance;

  public ErrorReport errors(List<UniqueError> errors) {
    this.errors = errors;
    return this;
  }

  public ErrorReport addErrorsItem(UniqueError errorsItem) {
    if (this.errors == null) {
      this.errors = new ArrayList<>();
    }
    this.errors.add(errorsItem);
    return this;
  }

}

