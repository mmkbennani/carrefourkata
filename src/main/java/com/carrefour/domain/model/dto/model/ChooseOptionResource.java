package com.carrefour.domain.model.dto.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * ChooseOptionResource
 */

@JsonTypeName("chooseOptionResource")  @Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChooseOptionResource {

  @JsonProperty("customerId")
  private BigDecimal customerId;

  /**
   * enum of delivery options
   */
  public enum DeliveryOptionEnum {
    DRIVE("DRIVE"),
    
    DELIVERY("DELIVERY"),
    
    DELIVERY_TODAY("DELIVERY_TODAY"),
    
    DELIVERY_ASAP("DELIVERY_ASAP");

    private String value;

    DeliveryOptionEnum(String value) {
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
    public static DeliveryOptionEnum fromValue(String value) {
      for (DeliveryOptionEnum b : DeliveryOptionEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  @JsonProperty("deliveryOption")
  private DeliveryOptionEnum deliveryOption;

  public ChooseOptionResource customerId(BigDecimal customerId) {
    this.customerId = customerId;
    return this;
  }

}

