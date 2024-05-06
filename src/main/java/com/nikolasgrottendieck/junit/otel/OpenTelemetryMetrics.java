package com.nikolasgrottendieck.junit.otel;

import com.nikolasgrottendieck.helper.Config;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.metrics.Meter;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;

import java.util.Optional;

public class OpenTelemetryMetrics implements TestWatcher {

	public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(Config.getMetricsNamespace());

	private final OpenTelemetry openTelemetry = GlobalOpenTelemetry.get();

	@SuppressWarnings("unused")
	private final Meter meter = openTelemetry.meterBuilder(OpenTelemetryMetrics.class.getPackageName())
		.setInstrumentationVersion(Config.getVersion())
		.build();

	@Override
	public void testDisabled(final ExtensionContext context, final Optional<String> reason) {
		TestWatcher.super.testDisabled(context, reason);
	}

	@Override
	public void testSuccessful(final ExtensionContext context) {
		TestWatcher.super.testSuccessful(context);
	}

	@Override
	public void testAborted(final ExtensionContext context, final Throwable cause) {
		TestWatcher.super.testAborted(context, cause);
	}

	@Override
	public void testFailed(final ExtensionContext context, final Throwable cause) {
		TestWatcher.super.testFailed(context, cause);
	}

}
