package com.nikolasgrottendieck.junit.otel;

import com.nikolasgrottendieck.helper.Config;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.metrics.LongCounter;
import io.opentelemetry.api.metrics.Meter;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;

import java.util.Optional;

/**
 * Creates Metrics based on the results of the JUnit tests executed that have either been annotated with {@link ObservedTests} or {@link org.junit.jupiter.api.extension.ExtendWith} referencing the {@link OpenTelemetryMetrics} class directly.
 */
public class OpenTelemetryMetrics implements TestWatcher {

	public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(Config.getMetricsNamespace());

	private final OpenTelemetry openTelemetry = GlobalOpenTelemetry.get();

	private final Meter meter = openTelemetry.meterBuilder(OpenTelemetryMetrics.class.getPackageName())
		.setInstrumentationVersion(Config.getVersion())
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
		testCounter.add(1);
		disabledCounter.add(1);
		TestWatcher.super.testDisabled(context, reason);
	}

	@Override
	public void testSuccessful(final ExtensionContext context) {
		testCounter.add(1);
		successCounter.add(1);
		TestWatcher.super.testSuccessful(context);
	}

	@Override
	public void testAborted(final ExtensionContext context, final Throwable cause) {
		testCounter.add(1);
		abortCounter.add(1);
		TestWatcher.super.testAborted(context, cause);
	}

	@Override
	public void testFailed(final ExtensionContext context, final Throwable cause) {
		testCounter.add(1);
		failureCounter.add(1);
		TestWatcher.super.testFailed(context, cause);
	}

}
