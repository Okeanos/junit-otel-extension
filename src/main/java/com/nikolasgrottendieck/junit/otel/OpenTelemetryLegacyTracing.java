package com.nikolasgrottendieck.junit.otel;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.DynamicTestInvocationContext;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.InvocationInterceptor;
import org.junit.jupiter.api.extension.ReflectiveInvocationContext;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import static com.nikolasgrottendieck.junit.otel.PropagationHelper.getParentSpan;

/**
 * Handles Tracing in JUnit tests based around the Lifecycle methods described
 * here: <a href="https://junit.org/junit5/docs/current/user-guide/#extensions-execution-order-overview">Extensions Execution Order</a>
 */
@Deprecated
public class OpenTelemetryLegacyTracing implements
	AfterAllCallback,
	AfterEachCallback,
	//AfterTestExecutionCallback,
	BeforeAllCallback,
	BeforeEachCallback,
	//BeforeTestExecutionCallback,
	InvocationInterceptor {

	public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create("opentelemetry-tracing");

	private final OpenTelemetry openTelemetry = GlobalOpenTelemetry.get();
	private final Tracer tracer = openTelemetry.tracerBuilder(OpenTelemetryLegacyTracing.class.getPackageName())
		.setInstrumentationVersion("0.0.1")
		.build();

	@Override
	public void afterAll(final ExtensionContext context) throws Exception {
		context.getStore(NAMESPACE).get(TestLifecycle.BEFORE_ALL.getSpanName(), Span.class).end();
		context.getStore(NAMESPACE).get(TestLifecycle.BEFORE_ALL.getScopeName(), Scope.class).close();
	}

	@Override
	public void afterEach(final ExtensionContext context) throws Exception {
		context.getStore(NAMESPACE).get(TestLifecycle.BEFORE_EACH.getSpanName(), Span.class).end();
		context.getStore(NAMESPACE).get(TestLifecycle.BEFORE_EACH.getScopeName(), Scope.class).close();
	}

//	@Override
//	public void afterTestExecution(final ExtensionContext context) throws Exception {
//		Span parentSpan = getParentSpan(TestLifecycle.AFTER_EXECUTION, context);
//		Context otelContext = null;
//		if (parentSpan != null) {
//			otelContext = Context.current().with(parentSpan);
//		}
//		Span span = TracingHelper.startSpan(otelContext, context, TestLifecycle.AFTER_EXECUTION, tracer);
//
//		context.getStore(NAMESPACE).put(TestLifecycle.AFTER_EXECUTION.getScopeName(), span.makeCurrent());
//		context.getStore(NAMESPACE).put(TestLifecycle.AFTER_EXECUTION.getSpanName(), span);
//	}

	@SuppressWarnings("MustBeClosedChecker")
	@Override
	public void beforeAll(final ExtensionContext context) throws Exception {
		Span parentSpan = getParentSpan(TestLifecycle.BEFORE_ALL, context);
		Context otelContext = null;
		if (parentSpan != null) {
			otelContext = Context.current().with(parentSpan);
		}
		Span span = TracingHelper.startSpan(otelContext, context, TestLifecycle.BEFORE_ALL, tracer);

		context.getStore(NAMESPACE).put(TestLifecycle.BEFORE_ALL.getScopeName(), span.makeCurrent());
		context.getStore(NAMESPACE).put(TestLifecycle.BEFORE_ALL.getSpanName(), span);
	}

	@SuppressWarnings("MustBeClosedChecker")
	@Override
	public void beforeEach(final ExtensionContext context) throws Exception {
		Span parentSpan = getParentSpan(TestLifecycle.BEFORE_EACH, context);
		Context otelContext = null;
		if (parentSpan != null) {
			otelContext = Context.current().with(parentSpan);
		}
		Span span = TracingHelper.startSpan(otelContext, context, TestLifecycle.BEFORE_EACH, tracer);

		context.getStore(NAMESPACE).put(TestLifecycle.BEFORE_EACH.getScopeName(), span.makeCurrent());
		context.getStore(NAMESPACE).put(TestLifecycle.BEFORE_EACH.getSpanName(), span);
	}

//	@Override
//	public void beforeTestExecution(final ExtensionContext context) throws Exception {
//		Span parentSpan = getParentSpan(TestLifecycle.BEFORE_EXECUTION, context);
//		Context otelContext = null;
//		if (parentSpan != null) {
//			otelContext = Context.current().with(parentSpan);
//		}
//		Span span = TracingHelper.startSpan(otelContext, context, TestLifecycle.BEFORE_EXECUTION, tracer);
//
//		context.getStore(NAMESPACE).put(TestLifecycle.BEFORE_EXECUTION.getScopeName(), span.makeCurrent());
//		context.getStore(NAMESPACE).put(TestLifecycle.BEFORE_EXECUTION.getSpanName(), span);
//	}

	@Override
	public <T> T interceptTestClassConstructor(final Invocation<T> invocation, final ReflectiveInvocationContext<Constructor<T>> invocationContext, final ExtensionContext extensionContext) throws Throwable {
		return TracingHelper.wrapWithSpan(
			invocation,
			invocationContext,
			extensionContext,
			tracer,
			TestLifecycle.TEST_CLASS
		);
	}

	@Override
	public void interceptBeforeAllMethod(final Invocation<Void> invocation, final ReflectiveInvocationContext<Method> invocationContext, final ExtensionContext extensionContext) throws Throwable {
		TracingHelper.wrapWithSpan(
			invocation,
			invocationContext,
			extensionContext,
			tracer,
			TestLifecycle.BEFORE_ALL
		);
	}

	@Override
	public void interceptBeforeEachMethod(final Invocation<Void> invocation, final ReflectiveInvocationContext<Method> invocationContext, final ExtensionContext extensionContext) throws Throwable {
		TracingHelper.wrapWithSpan(
			invocation,
			invocationContext,
			extensionContext,
			tracer,
			TestLifecycle.BEFORE_EACH
		);
	}

	@Override
	public void interceptTestMethod(final Invocation<Void> invocation, final ReflectiveInvocationContext<Method> invocationContext, final ExtensionContext extensionContext) throws Throwable {
		TracingHelper.wrapWithSpan(
			invocation,
			invocationContext,
			extensionContext,
			tracer,
			TestLifecycle.TEST_EXECUTION
		);
	}

	@Override
	public <T> T interceptTestFactoryMethod(final Invocation<T> invocation, final ReflectiveInvocationContext<Method> invocationContext, final ExtensionContext extensionContext) throws Throwable {
		return TracingHelper.wrapWithSpan(
			invocation,
			invocationContext,
			extensionContext,
			tracer,
			TestLifecycle.TEST_FACTORY
		);
	}

	@Override
	public void interceptTestTemplateMethod(final Invocation<Void> invocation, final ReflectiveInvocationContext<Method> invocationContext, final ExtensionContext extensionContext) throws Throwable {
		TracingHelper.wrapWithSpan(
			invocation,
			invocationContext,
			extensionContext,
			tracer,
			TestLifecycle.TEST_TEMPLATE
		);
	}

	@Override
	public void interceptDynamicTest(final Invocation<Void> invocation, final DynamicTestInvocationContext invocationContext, final ExtensionContext extensionContext) throws Throwable {
		TracingHelper.wrapWithSpan(
			invocation,
			null,
			extensionContext,
			tracer,
			TestLifecycle.DYNAMIC_TEST
		);
	}

	@Override
	public void interceptAfterEachMethod(final Invocation<Void> invocation, final ReflectiveInvocationContext<Method> invocationContext, final ExtensionContext extensionContext) throws Throwable {
		TracingHelper.wrapWithSpan(
			invocation,
			invocationContext,
			extensionContext,
			tracer,
			TestLifecycle.AFTER_EACH
		);
	}

	@Override
	public void interceptAfterAllMethod(final Invocation<Void> invocation, final ReflectiveInvocationContext<Method> invocationContext, final ExtensionContext extensionContext) throws Throwable {
		TracingHelper.wrapWithSpan(
			invocation,
			invocationContext,
			extensionContext,
			tracer,
			TestLifecycle.AFTER_ALL
		);
	}

}
