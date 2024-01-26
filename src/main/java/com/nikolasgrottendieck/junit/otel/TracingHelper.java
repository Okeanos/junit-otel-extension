package com.nikolasgrottendieck.junit.otel;

import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanBuilder;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.InvocationInterceptor;
import org.junit.jupiter.api.extension.ReflectiveInvocationContext;
import org.opentest4j.TestAbortedException;
import org.opentest4j.TestSkippedException;

final class TracingHelper {

	private TracingHelper() {
	}

	static Span startSpan(final ExtensionContext context, final TestLifecycle lifecycle, final Tracer tracer, final boolean isRootSpan) {
		SpanBuilder spanBuilder = tracer.spanBuilder(context.getDisplayName()).setSpanKind(SpanKind.INTERNAL);

		Span span = isRootSpan ? spanBuilder.setNoParent().startSpan() : spanBuilder.startSpan();

//		span.addEvent(
//			SemConName.LIFECYCLE.getOtelName(),
//			Attributes.of(
//				AttributeKey.stringKey(SemConName.LIFECYCLE.getOtelName()),
//				lifecycle.toString()
//			)
//		);

		span.setAttribute(SemConName.LIFECYCLE.getOtelName(), lifecycle.name());
		span.setAttribute(SemConName.UNIQUE_ID.getOtelName(), context.getUniqueId());
		context.getTags().forEach(t -> span.setAttribute(SemConName.TAG.getOtelName(), t));
		context.getTestClass().ifPresent(c -> span.setAttribute(SemConName.CLASS.getOtelName(), c.getCanonicalName()));
		context.getTestMethod().ifPresent(m -> span.setAttribute(SemConName.METHOD.getOtelName(), m.getName()));

		return span;
	}

	static <T> T startSpan(
		final InvocationInterceptor.Invocation<T> invocation,
		final ReflectiveInvocationContext<?> invocationContext,
		final ExtensionContext context,
		final Tracer tracer,
		final TestLifecycle lifecycle
	) throws Throwable {
		Span span = startSpan(context, lifecycle, tracer, false);

		try (Scope ignored = span.makeCurrent()) {
			if (invocationContext != null && !invocationContext.getArguments().isEmpty()) {
				span.setAllAttributes(Attributes.builder()
					.put(SemConName.ARGUMENTS.getOtelName(),
						invocationContext.getArguments().stream()
							.map(String::valueOf)
							.toArray(String[]::new)
					)
					.build()
				);
			}
			T returnValue = invocation.proceed();

			if (lifecycle == TestLifecycle.TEST_EXECUTION) {
				span.setAttribute(SemConName.RESULT.getOtelName(), TestResult.SUCCESSFUL.name());
				span.setStatus(StatusCode.OK);
			}
			return returnValue;
		} catch (Throwable e) {
			if (e instanceof TestAbortedException) {
				span.setAttribute(SemConName.RESULT.getOtelName(), TestResult.ABORTED.name());
				span.setStatus(StatusCode.UNSET);
			} else if (e instanceof TestSkippedException) {
				span.setAttribute(SemConName.RESULT.getOtelName(), TestResult.SKIPPED.name());
				span.setStatus(StatusCode.UNSET);
			} else {
				span.setAttribute(SemConName.RESULT.getOtelName(), TestResult.FAILED.name());
				span.setStatus(StatusCode.ERROR);
			}
			span.setAttribute(SemConName.RESULT_REASON.getOtelName(), e.getMessage());
			span.recordException(e);
			throw e;
		} finally {
			span.end();
		}
	}

}
