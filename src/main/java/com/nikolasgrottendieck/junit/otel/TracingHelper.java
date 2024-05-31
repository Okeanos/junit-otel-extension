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

		span.setAttribute(SemConName.LIFECYCLE.getOtelName(), lifecycle.name());
		span.setAttribute(SemConName.INSTANCE_LIFECYCLE.getOtelName(), instanceLifecycle.name());
		span.setAttribute(SemConName.UNIQUE_ID.getOtelName(), extensionContext.getUniqueId());
		extensionContext.getTags().forEach(t -> span.setAttribute(SemConName.TAG.getOtelName(), t));
		extensionContext.getTestClass().ifPresent(c -> span.setAttribute(SemConName.CLASS.getOtelName(), c.getCanonicalName()));
		extensionContext.getTestMethod().ifPresent(m -> span.setAttribute(SemConName.METHOD.getOtelName(), m.getName()));

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
					.put(SemConName.ARGUMENTS.getOtelName(),
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
				currentSpan.setAttribute(SemConName.RESULT.getOtelName(), TestResult.SUCCESSFUL.name());
				currentSpan.setStatus(StatusCode.OK);
			}
			return returnValue;
		} catch (Throwable e) {
			if (e instanceof TestAbortedException) {
				currentSpan.setAttribute(SemConName.RESULT.getOtelName(), TestResult.ABORTED.name());
				currentSpan.setStatus(StatusCode.UNSET);
			} else if (e instanceof TestSkippedException) {
				currentSpan.setAttribute(SemConName.RESULT.getOtelName(), TestResult.SKIPPED.name());
				currentSpan.setStatus(StatusCode.UNSET);
			} else {
				currentSpan.setAttribute(SemConName.RESULT.getOtelName(), TestResult.FAILED.name());
				currentSpan.setStatus(StatusCode.ERROR);
			}
			currentSpan.setAttribute(SemConName.RESULT_REASON.getOtelName(), e.getMessage());
			currentSpan.recordException(e);
			throw e;
		} finally {
			extensionContext.getStore(NAMESPACE).remove(lifecycle.getScopeName());
			extensionContext.getStore(NAMESPACE).remove(lifecycle.getSpanName());
			currentSpan.end();
		}
	}

}
