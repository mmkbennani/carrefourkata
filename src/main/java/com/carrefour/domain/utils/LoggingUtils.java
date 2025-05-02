package com.carrefour.domain.utils;

import org.slf4j.MDC;
import reactor.core.publisher.Signal;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.carrefour.domain.model.Constants.CORRELATION_ID_ATTR_NAME;

public class LoggingUtils {
    private LoggingUtils() {
        throw new IllegalStateException("Utility class");
    }

    private static <T, U> Consumer<Signal<T>> handleSignal(Predicate<Signal<T>> predicate,
                                                           Function<Signal<T>, U> signalFunction,
                                                           Consumer<U> logStatement) {
        return signal -> {
            if (predicate.test(signal)) {
                Optional<String> correlationIdMaybe = signal.getContextView().getOrEmpty(CORRELATION_ID_ATTR_NAME);

                correlationIdMaybe.ifPresentOrElse(correlationId -> {
                    try (MDC.MDCCloseable ignored = MDC.putCloseable(CORRELATION_ID_ATTR_NAME, correlationId)) {
                        logStatement.accept(signalFunction.apply(signal));
                    }
                }, () -> logStatement.accept(signalFunction.apply(signal)));
            }
        };
    }

    public static <T> Consumer<Signal<T>> logOnError(Consumer<Throwable> logStatement) {
        return handleSignal(Signal::isOnError, Signal::getThrowable, logStatement);
    }

    public static <T> Consumer<Signal<T>> logOnErrorWebclient(Consumer<Throwable> logStatement) {
        return handleSignal(Signal::isOnError, Signal::getThrowable, logStatement);
    }

    public static <T> Consumer<Signal<T>> logOnError(Class<? extends Throwable> exceptionType, Consumer<Throwable> logStatement) {
        return handleSignal(signal -> signal.isOnError() && exceptionType.isInstance(signal.getThrowable()), Signal::getThrowable, logStatement);
    }

    public static <T> Consumer<Signal<T>> logOnNext(Consumer<T> logStatement) {
        return handleSignal(Signal::isOnNext, Signal::get, logStatement);
    }
}
