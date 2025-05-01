package com.carrefour.domain.model.dto.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Error report following RFC 7807
 */

@JsonTypeName("errorReport_allOf")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ErrorReportAllOf {

  @JsonProperty("errors")
  @Valid
  private List<UniqueError> errors = null;

  public ErrorReportAllOf errors(List<UniqueError> errors) {
    this.errors = errors;
    return this;
  }

  public ErrorReportAllOf addErrorsItem(UniqueError errorsItem) {
    if (this.errors == null) {
      this.errors = new ArrayList<>();
    }
    this.errors.add(errorsItem);
    return this;
  }


}

