package com.nikolasgrottendieck.junit.otel;

import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Observe JUnit tests implicitly and create Traces ({@link OpenTelemetryTracing}) and Metrics ({@link OpenTelemetryMetrics}.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith({OpenTelemetryTracing.class, OpenTelemetryMetrics.class})
public @interface ObservedTests {
}
