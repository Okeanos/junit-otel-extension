package com.nikolasgrottendieck.junit.otel;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.propagation.TextMapGetter;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.util.Locale;
import java.util.Map;

import static com.nikolasgrottendieck.junit.otel.OpenTelemetryTracing.NAMESPACE;

/**
 * Helper class to facilitate OpenTelemetry context propagation within the extension.
 */
final class PropagationHelper {

	private PropagationHelper() {
	}

	/**
	 * Retrieves a propagated OpenTelemetry context from the system's environment that can be used as trace parent.
	 * Taken from {@link io.opentelemetry.maven#sessionStarted}.
	 *
	 * @param openTelemetry the OpenTelemetry instance
	 * @return the trace parent or Null
	 */
	static Context getOutsideContext(final OpenTelemetry openTelemetry) {
		TextMapGetter<Map<String, String>> toUpperCaseTextMapGetter = new ToUpperCaseTextMapGetter();

		return
			openTelemetry
				.getPropagators()
				.getTextMapPropagator()
				.extract(
					Context.current(),
					System.getenv(),
					toUpperCaseTextMapGetter);
	}

	/**
	 * Retrieves the closest parent span from the JUnit extension {@link org.junit.jupiter.api.extension.ExtensionContext.Store} so it can be used as parent.
	 *
	 * @param lifecycle current test lifecycle state
	 * @param context   the JUnit extension context to access the {@link org.junit.jupiter.api.extension.ExtensionContext.Store}
	 * @return the parent span or Null
	 */
	@Nullable
	static Span getParentSpan(final TestLifecycle lifecycle, final ExtensionContext context) {
		if (lifecycle.getParent() == null) {
			return null;
		}
		Span parentSpan = context.getStore(NAMESPACE).get(lifecycle.getParent().getSpanName(), Span.class);
		if (parentSpan == null) {
			return getParentSpan(lifecycle.getParent(), context);
		}

		return parentSpan;
	}

	/**
	 * Normalizes the system's environment to be able to consistently extract and use the trace parent / trace context information.
	 * Taken from {@link io.opentelemetry.maven#ToUpperCaseTextMapGetter}.
	 */
	private static class ToUpperCaseTextMapGetter implements TextMapGetter<Map<String, String>> {
		@Override
		public Iterable<String> keys(Map<String, String> environmentVariables) {
			return environmentVariables.keySet();
		}

		@Override
		@Nullable
		public String get(@Nullable Map<String, String> environmentVariables, @NonNull String key) {
			return environmentVariables == null
				? null
				: environmentVariables.get(key.toUpperCase(Locale.ROOT));
		}
	}

}
