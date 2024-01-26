package com.nikolasgrottendieck.junit.otel;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.DynamicTestInvocationContext;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.InvocationInterceptor;
import org.junit.jupiter.api.extension.ReflectiveInvocationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * Handles Tracing in JUnit tests based around the Lifecycle methods described
 * here: <a href="https://junit.org/junit5/docs/current/user-guide/#extensions-execution-order-overview">Extensions Execution Order</a>
 */
public class OpenTelemetryTracing implements
	AfterAllCallback,
	AfterEachCallback,
	//AfterTestExecutionCallback,
	BeforeAllCallback,
	BeforeEachCallback,
	//BeforeTestExecutionCallback,
	InvocationInterceptor {

	public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create("opentelemetry-tracing");
	private static final Logger LOGGER = LoggerFactory.getLogger(OpenTelemetryTracing.class);

	private final OpenTelemetry openTelemetry = GlobalOpenTelemetry.get();
	private final Tracer tracer = openTelemetry.tracerBuilder(OpenTelemetryTracing.class.getPackageName())
		.setInstrumentationVersion("0.0.1")
		.build();

	@Override
	public void afterAll(final ExtensionContext context) throws Exception {
		LOGGER.debug("closing current span");
		context.getStore(NAMESPACE).get("currentSpan", Span.class).end();
		context.getStore(NAMESPACE).get("scope", Scope.class).close();
	}

	@Override
	public void afterEach(final ExtensionContext context) throws Exception {
		LOGGER.debug("closing current span");
		context.getStore(NAMESPACE).get("currentSpan", Span.class).end();
		context.getStore(NAMESPACE).get("scope", Scope.class).close();
	}

//	@Override
//	public void afterTestExecution(final ExtensionContext context) throws Exception {
//		LOGGER.debug("closing current span");
//		context.getStore(NAMESPACE).get("currentSpan", Span.class).end();
//		context.getStore(NAMESPACE).get("scope", Scope.class).close();
//	}

	@SuppressWarnings("MustBeClosedChecker")
	@Override
	public void beforeAll(final ExtensionContext context) throws Exception {
		LOGGER.debug("opening new span");
		Span span = context.getStore(NAMESPACE).getOrComputeIfAbsent("currentSpan", Span -> TracingHelper.startSpan(context, TestLifecycle.BEFORE_ALL, tracer, true), Span.class);

		context.getStore(NAMESPACE).put("scope", span.makeCurrent());
		context.getStore(NAMESPACE).put("currentSpan", span);
	}

	@SuppressWarnings("MustBeClosedChecker")
	@Override
	public void beforeEach(final ExtensionContext context) throws Exception {
		LOGGER.debug("opening new span");
		Span span = TracingHelper.startSpan(context, TestLifecycle.BEFORE_EACH, tracer, false);

		context.getStore(NAMESPACE).put("scope", span.makeCurrent());
		context.getStore(NAMESPACE).put("currentSpan", span);
	}

//	@Override
//	public void beforeTestExecution(final ExtensionContext context) throws Exception {
//		LOGGER.debug("opening new span");
//		Span span = TracingHelper.startSpan(context, TestLifecycle.TEST, tracer, false);
//
//		context.getStore(NAMESPACE).put("scope", span.makeCurrent());
//		context.getStore(NAMESPACE).put("currentSpan", span);
//	}

	@Override
	public <T> T interceptTestClassConstructor(final Invocation<T> invocation, final ReflectiveInvocationContext<Constructor<T>> invocationContext, final ExtensionContext extensionContext) throws Throwable {
		LOGGER.debug("opening new span");
		extensionContext.getStore(NAMESPACE).getOrComputeIfAbsent("currentSpan", Span -> TracingHelper.startSpan(extensionContext, TestLifecycle.CLASS_CONSTRUCTOR, tracer, true), Span.class);

		return TracingHelper.startSpan(invocation, invocationContext, extensionContext, tracer, TestLifecycle.CLASS_CONSTRUCTOR);
	}

	@Override
	public void interceptBeforeAllMethod(final Invocation<Void> invocation, final ReflectiveInvocationContext<Method> invocationContext, final ExtensionContext extensionContext) throws Throwable {
		LOGGER.debug("opening new span");
		TracingHelper.startSpan(invocation, invocationContext, extensionContext, tracer, TestLifecycle.BEFORE_ALL);
	}

	@Override
	public void interceptBeforeEachMethod(final Invocation<Void> invocation, final ReflectiveInvocationContext<Method> invocationContext, final ExtensionContext extensionContext) throws Throwable {
		LOGGER.debug("opening new span");
		TracingHelper.startSpan(invocation, invocationContext, extensionContext, tracer, TestLifecycle.BEFORE_EACH);
	}

	@Override
	public void interceptTestMethod(final Invocation<Void> invocation, final ReflectiveInvocationContext<Method> invocationContext, final ExtensionContext extensionContext) throws Throwable {
		TracingHelper.startSpan(invocation, invocationContext, extensionContext, tracer, TestLifecycle.TEST_EXECUTION);
	}

	@Override
	public <T> T interceptTestFactoryMethod(final Invocation<T> invocation, final ReflectiveInvocationContext<Method> invocationContext, final ExtensionContext extensionContext) throws Throwable {
		return TracingHelper.startSpan(invocation, invocationContext, extensionContext, tracer, TestLifecycle.FACTORY_METHOD);
	}

	@Override
	public void interceptTestTemplateMethod(final Invocation<Void> invocation, final ReflectiveInvocationContext<Method> invocationContext, final ExtensionContext extensionContext) throws Throwable {
		TracingHelper.startSpan(invocation, invocationContext, extensionContext, tracer, TestLifecycle.TEST_EXECUTION);
	}

	@Override
	public void interceptDynamicTest(final Invocation<Void> invocation, final DynamicTestInvocationContext invocationContext, final ExtensionContext extensionContext) throws Throwable {
		TracingHelper.startSpan(invocation, null, extensionContext, tracer, TestLifecycle.TEST_EXECUTION);
	}

	@Override
	public void interceptAfterEachMethod(final Invocation<Void> invocation, final ReflectiveInvocationContext<Method> invocationContext, final ExtensionContext extensionContext) throws Throwable {
		TracingHelper.startSpan(invocation, invocationContext, extensionContext, tracer, TestLifecycle.AFTER_EACH);
	}

	@Override
	public void interceptAfterAllMethod(final Invocation<Void> invocation, final ReflectiveInvocationContext<Method> invocationContext, final ExtensionContext extensionContext) throws Throwable {
		TracingHelper.startSpan(invocation, invocationContext, extensionContext, tracer, TestLifecycle.AFTER_ALL);
	}

}
