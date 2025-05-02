package com.carrefour.domain.model.dto.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

/**
 * BookTimeSlot400Response
 */

@JsonTypeName("bookTimeSlot_400_response")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookTimeSlot400Response {

  @JsonProperty("title")
  private String title = "Bad request.";

  /**
   * optional clear text describing the detail of the error.
   */
  public enum DetailsEnum {
    CUSTOMERID_FIELD_IS_MISSING("customerId field is missing"),
    
    TIMESLOTID_FIELD_IS_MISSING("timeSlotId field is missing");

    private String value;

    DetailsEnum(String value) {
      this.value = value;
    }

    @JsonValue
    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static DetailsEnum fromValue(String value) {
      for (DetailsEnum b : DetailsEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  @JsonProperty("details")
  private DetailsEnum details;

  /**
   * this technical code is used to identify the error type, useful for automated testing
   */
  public enum TypeEnum {
    _001_INVALID_CUSTOMERID("https://carrefour.tech/errors/DD-001-invalid-customerId"),
    
    _002_INVALID_TIMESLOTID("https://carrefour.tech/errors/DD-002-invalid-timeSlotId");

    private String value;

    TypeEnum(String value) {
      this.value = value;
    }

    @JsonValue
    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static TypeEnum fromValue(String value) {
      for (TypeEnum b : TypeEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  @JsonProperty("type")
  private TypeEnum type;

}

