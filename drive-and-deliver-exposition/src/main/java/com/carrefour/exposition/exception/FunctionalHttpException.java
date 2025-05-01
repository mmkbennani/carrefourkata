package com.carrefour.exposition.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class FunctionalHttpException extends RuntimeException {
}
