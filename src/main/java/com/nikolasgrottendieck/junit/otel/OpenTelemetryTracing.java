package com.nikolasgrottendieck.junit.otel;

import com.nikolasgrottendieck.helper.Config;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Tracer;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.InvocationInterceptor;
import org.junit.jupiter.api.extension.ReflectiveInvocationContext;
import org.junit.jupiter.api.extension.TestInstanceFactoryContext;
import org.junit.jupiter.api.extension.TestInstancePreConstructCallback;
import org.junit.jupiter.api.extension.TestInstancePreDestroyCallback;

import java.lang.reflect.Method;

public class OpenTelemetryTracing implements
	TestInstancePreConstructCallback,
	InvocationInterceptor,
	TestInstancePreDestroyCallback {

	public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(Config.getTracingNamespace());

	private final OpenTelemetry openTelemetry = GlobalOpenTelemetry.get();

	@SuppressWarnings("unused")
	private final Tracer tracer = openTelemetry.tracerBuilder(OpenTelemetryTracing.class.getPackageName())
		.setInstrumentationVersion(Config.getVersion())
		.build();

	@Override
	public void preConstructTestInstance(final TestInstanceFactoryContext factoryContext, final ExtensionContext context) {
	}

	@Override
	public void interceptTestMethod(final Invocation<Void> invocation, final ReflectiveInvocationContext<Method> invocationContext, final ExtensionContext extensionContext) throws Throwable {
	}

	@Override
	public void preDestroyTestInstance(final ExtensionContext context) throws Exception {

	}

}
