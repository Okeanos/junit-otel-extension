package com.nikolasgrottendieck.junit.otel;

import com.nikolasgrottendieck.helper.Config;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.InvocationInterceptor;
import org.junit.jupiter.api.extension.ReflectiveInvocationContext;
import org.junit.jupiter.api.extension.TestInstanceFactoryContext;
import org.junit.jupiter.api.extension.TestInstancePreConstructCallback;
import org.junit.jupiter.api.extension.TestInstancePreDestroyCallback;

import java.lang.reflect.Method;

import static com.nikolasgrottendieck.junit.otel.PropagationHelper.getParentSpan;

/**
 * Handles Tracing in JUnit tests based around the Lifecycle methods described
 * here: <a href="https://junit.org/junit5/docs/current/user-guide/#extensions-execution-order-overview">Extensions Execution Order</a>
 * <p>
 * Traces are created for classes that have either been annotated with {@link ObservedTests} or {@link org.junit.jupiter.api.extension.ExtendWith} referencing the {@link OpenTelemetryTracing} class directly.
 */
public class OpenTelemetryTracing implements
	TestInstancePreConstructCallback,
	InvocationInterceptor,
	TestInstancePreDestroyCallback {

	public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(Config.getTracingNamespace());

	private final OpenTelemetry openTelemetry = GlobalOpenTelemetry.get();
	private final Tracer tracer = openTelemetry.tracerBuilder(OpenTelemetryTracing.class.getPackageName())
		.setInstrumentationVersion(Config.getVersion())
		.build();

	@SuppressWarnings("MustBeClosedChecker")
	@Override
	public void preConstructTestInstance(final TestInstanceFactoryContext factoryContext, final ExtensionContext context) {
		Span parentSpan = getParentSpan(TestLifecycle.PRE_INSTANCE_CONSTRUCT, context);
		Context otelContext = null;

		if (parentSpan != null) {
			otelContext = Context.current().with(parentSpan);
		}

		Span span = TracingHelper.startSpan(
			otelContext,
			context,
			TestLifecycle.PRE_INSTANCE_CONSTRUCT,
			tracer
		);

		context.getStore(NAMESPACE).put(TestLifecycle.PRE_INSTANCE_CONSTRUCT.getScopeName(), span.makeCurrent());
		context.getStore(NAMESPACE).put(TestLifecycle.PRE_INSTANCE_CONSTRUCT.getSpanName(), span);
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
	public void preDestroyTestInstance(final ExtensionContext context) throws Exception {
		context.getStore(NAMESPACE).get(TestLifecycle.PRE_INSTANCE_CONSTRUCT.getSpanName(), Span.class).end();
		context.getStore(NAMESPACE).get(TestLifecycle.PRE_INSTANCE_CONSTRUCT.getScopeName(), Scope.class).close();
	}

}
