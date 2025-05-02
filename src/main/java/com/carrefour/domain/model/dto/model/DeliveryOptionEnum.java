package com.carrefour.domain.model.dto.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

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
    @JsonCreator
    public static  boolean isDRIVE(DeliveryOptionEnum deliveryMethod) {
        return DeliveryOptionEnum.DRIVE.equals(deliveryMethod);
    }

    @JsonCreator
    public static  boolean isDELIVERY(DeliveryOptionEnum deliveryMethod) {
        return DeliveryOptionEnum.DELIVERY.equals(deliveryMethod);
    }

    @JsonCreator
    public static  boolean isDELIVERYTODAY(DeliveryOptionEnum deliveryMethod) {
        return DeliveryOptionEnum.DELIVERY_TODAY.equals(deliveryMethod);
    }

    @JsonCreator
    public static  boolean isDELIVERYASAP(DeliveryOptionEnum deliveryMethod) {
        return DeliveryOptionEnum.DELIVERY_ASAP.equals(deliveryMethod);
    }
}
