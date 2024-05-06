package com.nikolasgrottendieck.junit.otel;

import io.opentelemetry.api.trace.Span;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.extension.ExtensionContext;

import static com.nikolasgrottendieck.junit.otel.OpenTelemetryTracing.NAMESPACE;

final class PropagationHelper {

	private PropagationHelper() {
	}

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
