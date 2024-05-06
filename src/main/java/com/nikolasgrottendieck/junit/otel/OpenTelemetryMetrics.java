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

	private final LongCounter testCounter = meter.counterBuilder("org.junit.test.counter")
		.setDescription("Counts the number of tests")
		.setUnit("Test")
		.build();

	private final LongCounter disabledCounter = meter.counterBuilder("org.junit.test.disabled")
		.setDescription("Counts the number of disabled tests")
		.setUnit("Test")
		.build();

	private final LongCounter successCounter = meter.counterBuilder("org.junit.test.successful")
		.setDescription("Counts the number of successful tests")
		.setUnit("Test")
		.build();

	private final LongCounter failureCounter = meter.counterBuilder("org.junit.test.failed")
		.setDescription("Counts the number of failed tests")
		.setUnit("Test")
		.build();

	private final LongCounter abortCounter = meter.counterBuilder("org.junit.test.aborted")
		.setDescription("Counts the number of aborted tests")
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
