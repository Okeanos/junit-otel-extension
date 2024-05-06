package com.nikolasgrottendieck.junit.otel;

import io.opentelemetry.api.trace.Span;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.extension.ExtensionContext;

import static com.nikolasgrottendieck.junit.otel.OpenTelemetryTracing.NAMESPACE;

/**
 * Helper class to facilitate OpenTelemetry context propagation within the extension.
 */
final class PropagationHelper {

	private PropagationHelper() {
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

}
