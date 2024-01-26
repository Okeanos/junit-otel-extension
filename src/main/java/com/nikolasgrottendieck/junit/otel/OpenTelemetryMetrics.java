package com.nikolasgrottendieck.junit.otel;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.metrics.LongCounter;
import io.opentelemetry.api.metrics.Meter;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class OpenTelemetryMetrics implements TestWatcher {

	public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create("opentelemetry-metrics");

	private static final Logger LOGGER = LoggerFactory.getLogger(OpenTelemetryMetrics.class);

	private final OpenTelemetry openTelemetry = GlobalOpenTelemetry.get();

	private final Meter meter = openTelemetry.meterBuilder(OpenTelemetryMetrics.class.getPackageName())
		.setInstrumentationVersion("0.0.1")
		.build();

	private final LongCounter testCounter = meter.counterBuilder(SemConName.TEST_COUNTER.getOtelName())
		.setDescription(SemConName.TEST_COUNTER.getDescription())
		.setUnit("Test")
		.build();

	private final LongCounter disabledCounter = meter.counterBuilder(SemConName.TEST_DISABLED_COUNTER.getOtelName())
		.setDescription(SemConName.TEST_DISABLED_COUNTER.getDescription())
		.setUnit("Test")
		.build();

	private final LongCounter successCounter = meter.counterBuilder(SemConName.TEST_SUCCESS_COUNTER.getOtelName())
		.setDescription(SemConName.TEST_SUCCESS_COUNTER.getDescription())
		.setUnit("Test")
		.build();

	private final LongCounter failureCounter = meter.counterBuilder(SemConName.TEST_FAILURE_COUNTER.getOtelName())
		.setDescription(SemConName.TEST_FAILURE_COUNTER.getDescription())
		.setUnit("Test")
		.build();

	private final LongCounter abortCounter = meter.counterBuilder(SemConName.TEST_ABORT_COUNTER.getOtelName())
		.setDescription(SemConName.TEST_ABORT_COUNTER.getDescription())
		.setUnit("Test")
		.build();

	@Override
	public void testDisabled(final ExtensionContext context, final Optional<String> reason) {
		LOGGER.debug("disabled test encountered");
		testCounter.add(1);
		disabledCounter.add(1);
		TestWatcher.super.testDisabled(context, reason);
	}

	@Override
	public void testSuccessful(final ExtensionContext context) {
		LOGGER.debug("successful test encountered");
		testCounter.add(1);
		successCounter.add(1);
		TestWatcher.super.testSuccessful(context);
	}

	@Override
	public void testAborted(final ExtensionContext context, final Throwable cause) {
		LOGGER.debug("aborted test encountered");
		testCounter.add(1);
		abortCounter.add(1);
		TestWatcher.super.testAborted(context, cause);
	}

	@Override
	public void testFailed(final ExtensionContext context, final Throwable cause) {
		LOGGER.debug("failed test encountered");
		testCounter.add(1);
		failureCounter.add(1);
		TestWatcher.super.testFailed(context, cause);
	}
}
