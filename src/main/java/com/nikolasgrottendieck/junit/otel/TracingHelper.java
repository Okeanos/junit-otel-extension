package com.nikolasgrottendieck.junit.otel;

import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanBuilder;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.InvocationInterceptor;
import org.junit.jupiter.api.extension.ReflectiveInvocationContext;
import org.opentest4j.TestAbortedException;
import org.opentest4j.TestSkippedException;

import static com.nikolasgrottendieck.junit.otel.OpenTelemetryTracing.NAMESPACE;
import static com.nikolasgrottendieck.junit.otel.PropagationHelper.getParentSpan;

final class TracingHelper {

	private TracingHelper() {
	}

	static Span startSpan(
		@Nullable final Context otelContext,
		final ExtensionContext extensionContext,
		final TestLifecycle lifecycle,
		final Tracer tracer
	) {
		SpanBuilder spanBuilder = tracer.spanBuilder(extensionContext.getDisplayName()).setSpanKind(SpanKind.INTERNAL);

		Span span = spanBuilder.setParent(otelContext).startSpan();
		TestInstance.Lifecycle instanceLifecycle = extensionContext.getTestInstanceLifecycle().orElse(TestInstance.Lifecycle.PER_CLASS);

		span.setAttribute("org.junit.test.lifecycle", lifecycle.name());
		span.setAttribute("org.junit.test.instance_lifecycle", instanceLifecycle.name());
		span.setAttribute("org.junit.test.unique_id", extensionContext.getUniqueId());
		extensionContext.getTags().forEach(t -> span.setAttribute("org.junit.test.tag", t));
		extensionContext.getTestClass().ifPresent(c -> span.setAttribute("org.junit.test.class", c.getCanonicalName()));
		extensionContext.getTestMethod().ifPresent(m -> span.setAttribute("org.junit.test.method", m.getName()));

		return span;
	}

	static <T> T wrapWithSpan(
		final InvocationInterceptor.Invocation<T> invocation,
		final ReflectiveInvocationContext<?> invocationContext,
		final ExtensionContext extensionContext,
		final Tracer tracer,
		final TestLifecycle lifecycle
	) throws Throwable {
		Span parentSpan = getParentSpan(lifecycle, extensionContext);
		Context otelContext = parentSpan == null ? null : Context.current().with(parentSpan);
		Span currentSpan = startSpan(otelContext, extensionContext, lifecycle, tracer);

		try (Scope scope = currentSpan.makeCurrent()) {
			if (invocationContext != null && !invocationContext.getArguments().isEmpty()) {
				currentSpan.setAllAttributes(Attributes.builder()
					.put("org.junit.test.arguments",
						invocationContext.getArguments().stream()
							.map(String::valueOf)
							.toArray(String[]::new)
					)
					.build()
				);
			}

			extensionContext.getStore(NAMESPACE).put(lifecycle.getScopeName(), scope);
			extensionContext.getStore(NAMESPACE).put(lifecycle.getSpanName(), currentSpan);

			T returnValue = invocation.proceed();

			if (lifecycle == TestLifecycle.TEST_EXECUTION) {
				currentSpan.setAttribute("org.junit.test.result", "SUCCESSFUL");
				currentSpan.setStatus(StatusCode.OK);
			}
			return returnValue;
		} catch (Throwable e) {
			if (e instanceof TestAbortedException) {
				currentSpan.setAttribute("org.junit.test.result", "ABORTED");
				currentSpan.setStatus(StatusCode.UNSET);
			} else if (e instanceof TestSkippedException) {
				currentSpan.setAttribute("org.junit.test.result", "SKIPPED");
				currentSpan.setStatus(StatusCode.UNSET);
			} else {
				currentSpan.setAttribute("org.junit.test.result", "FAILED");
				currentSpan.setStatus(StatusCode.ERROR);
			}
			currentSpan.setAttribute("org.junit.test.result.reason", e.getMessage());
			currentSpan.recordException(e);
			throw e;
		} finally {
			extensionContext.getStore(NAMESPACE).remove(lifecycle.getScopeName());
			extensionContext.getStore(NAMESPACE).remove(lifecycle.getSpanName());
			currentSpan.end();
		}
	}

}
