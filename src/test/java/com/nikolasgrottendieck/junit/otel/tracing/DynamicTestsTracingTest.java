package com.nikolasgrottendieck.junit.otel.tracing;

import com.nikolasgrottendieck.junit.otel.OpenTelemetryTracing;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.logs.Logger;
import io.opentelemetry.api.metrics.Meter;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.sdk.testing.junit5.OpenTelemetryExtension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.engine.descriptor.JupiterEngineDescriptor;
import org.junit.platform.testkit.engine.EngineTestKit;

import java.util.stream.Stream;

import static com.nikolasgrottendieck.junit.otel.SemConName.CLASS;
import static com.nikolasgrottendieck.junit.otel.SemConName.LIFECYCLE;
import static com.nikolasgrottendieck.junit.otel.SemConName.UNIQUE_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.MapAssert.assertThatMap;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

public class DynamicTestsTracingTest {

	@RegisterExtension
	private static final OpenTelemetryExtension otelTesting = OpenTelemetryExtension.create();

	private final Tracer tracer = otelTesting.getOpenTelemetry().getTracer("test");
	private final Meter meter = otelTesting.getOpenTelemetry().getMeter("test");
	private final Logger logger = otelTesting.getOpenTelemetry().getLogsBridge().get("test");

	@Test
	void verifySpans() {
		EngineTestKit
			.engine(JupiterEngineDescriptor.ENGINE_ID)
			.selectors(selectClass(TracingDynamicTestsExampleTest.class))
			.execute()
			.testEvents()
			.assertStatistics(stats ->
				stats.started(1)
					.failed(1)
			);

		assertThat(otelTesting.getSpans())
			.satisfiesExactly(
				spanData -> {
					assertThat(spanData.getName()).isEqualTo("DynamicTestsTracingTest$TracingDynamicTestsExampleTest");
					assertThatMap(spanData.getAttributes().asMap())
						.containsKeys(
							AttributeKey.stringKey(CLASS.getOtelName()),
							AttributeKey.stringKey(LIFECYCLE.getOtelName()),
							AttributeKey.stringKey(UNIQUE_ID.getOtelName())
						)
					;
				},
				spanData -> {
					assertThat(spanData.getName()).isEqualTo("dynamicTestsFromIntStream()");
					assertThatMap(spanData.getAttributes().asMap())
						.containsKeys(
							AttributeKey.stringKey(CLASS.getOtelName()),
							AttributeKey.stringKey(LIFECYCLE.getOtelName()),
							AttributeKey.stringKey(UNIQUE_ID.getOtelName())
						)
					;
				},
				spanData -> {
					assertThat(spanData.getName()).isEqualTo("dynamicTest");
					assertThatMap(spanData.getAttributes().asMap())
						.containsKeys(
							AttributeKey.stringKey(LIFECYCLE.getOtelName()),
							AttributeKey.stringKey(UNIQUE_ID.getOtelName())
						)
					;
				},
				spanData -> {
					assertThat(spanData.getName()).isEqualTo("dynamicTestsFromIntStream()");
					assertThatMap(spanData.getAttributes().asMap())
						.containsKeys(
							AttributeKey.stringKey(CLASS.getOtelName()),
							AttributeKey.stringKey(LIFECYCLE.getOtelName()),
							AttributeKey.stringKey(UNIQUE_ID.getOtelName())
						)
					;
				},
				spanData -> {
					assertThat(spanData.getName()).isEqualTo("DynamicTestsTracingTest$TracingDynamicTestsExampleTest");
					assertThatMap(spanData.getAttributes().asMap())
						.containsKeys(
							AttributeKey.stringKey(CLASS.getOtelName()),
							AttributeKey.stringKey(LIFECYCLE.getOtelName()),
							AttributeKey.stringKey(UNIQUE_ID.getOtelName())
						)
					;
				}
			);
	}

	@ExtendWith(OpenTelemetryTracing.class)
	static class TracingDynamicTestsExampleTest {

		@TestFactory
		Stream<DynamicContainer> dynamicTestsFromIntStream() {
			return Stream.of(DynamicContainer.dynamicContainer("dynamic-container",
				Stream.of(DynamicTest.dynamicTest("dynamicTest", Assertions::fail))));
		}
	}

}
