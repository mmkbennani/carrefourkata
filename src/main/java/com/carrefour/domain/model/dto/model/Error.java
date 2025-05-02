package com.carrefour.domain.model.dto.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

/**
 * Error
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Error {

  /**
   * The error code
   */
  public enum TypeEnum {
    _01("https://carrefour.tech/errors/DR-ERR-01"),
    
    _02("https://carrefour.tech/errors/DR-ERR-02"),
    
    _03("https://carrefour.tech/errors/DR-ERR-03"),
    
    _04("https://carrefour.tech/errors/DR-ERR-04"),
    
    _05("https://carrefour.tech/errors/DR-ERR-05"),
    
    _06("https://carrefour.tech/errors/DR-ERR-06"),
    
    _07("https://carrefour.tech/errors/DR-ERR-07"),
    
    _08("https://carrefour.tech/errors/DR-ERR-08"),
    
    _09("https://carrefour.tech/errors/DR-ERR-09");

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

  @JsonProperty("title")
  private String title;

  @JsonProperty("details")
  private String details;

  @JsonProperty("instance")
  private String instance;

  public Error type(TypeEnum type) {
    this.type = type;
    return this;
  }

}

