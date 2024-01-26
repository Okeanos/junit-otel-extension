package com.nikolasgrottendieck.junit.otel;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.InvocationInterceptor;
import org.junit.jupiter.api.extension.ReflectiveInvocationContext;
import org.junit.jupiter.api.extension.TestInstanceFactoryContext;
import org.junit.jupiter.api.extension.TestInstancePreConstructCallback;
import org.junit.jupiter.api.extension.TestInstancePreDestroyCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * Handles Tracing in JUnit tests based around the Lifecycle methods described
 * here: <a href="https://junit.org/junit5/docs/current/user-guide/#extensions-execution-order-overview">Extensions Execution Order</a>
 */
public class OpenTelemetrySimpleTracing implements
	TestInstancePreConstructCallback,
	InvocationInterceptor,
	TestInstancePreDestroyCallback {

	public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create("opentelemetry-tracing");
	private static final Logger LOGGER = LoggerFactory.getLogger(OpenTelemetrySimpleTracing.class);

	private final OpenTelemetry openTelemetry = GlobalOpenTelemetry.get();
	private final Tracer tracer = openTelemetry.tracerBuilder(OpenTelemetrySimpleTracing.class.getPackageName())
		.setInstrumentationVersion("0.0.1")
		.build();

	@SuppressWarnings("MustBeClosedChecker")
	@Override
	public void preConstructTestInstance(final TestInstanceFactoryContext factoryContext, final ExtensionContext context) {
		LOGGER.debug("opening root span");
		Span span = context.getStore(NAMESPACE).getOrComputeIfAbsent("currentSpan", Span -> TracingHelper.startSpan(context, TestLifecycle.PRE_INSTANCE_CONSTRUCT, tracer, true), Span.class);

		context.getStore(NAMESPACE).put("scope", span.makeCurrent());
		context.getStore(NAMESPACE).put("currentSpan", span);
	}

	@Override
	public void interceptTestMethod(final Invocation<Void> invocation, final ReflectiveInvocationContext<Method> invocationContext, final ExtensionContext extensionContext) throws Throwable {
		TracingHelper.startSpan(invocation, invocationContext, extensionContext, tracer, TestLifecycle.TEST_EXECUTION);
	}

	@Override
	public void preDestroyTestInstance(final ExtensionContext context) throws Exception {
		LOGGER.debug("closing root span");
		context.getStore(NAMESPACE).get("currentSpan", Span.class).end();
		context.getStore(NAMESPACE).get("scope", Scope.class).close();
	}
}
