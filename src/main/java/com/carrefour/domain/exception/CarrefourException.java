package com.carrefour.domain.exception;

import lombok.*;

import java.io.IOException;
import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CarrefourException extends RuntimeException implements Serializable {
    private CarrefourError carrefourError;

    private void writeObject(java.io.ObjectOutputStream stream)
        throws IOException {
        stream.defaultWriteObject();
    }

    private void readObject(java.io.ObjectInputStream stream)
        throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
    }
}
