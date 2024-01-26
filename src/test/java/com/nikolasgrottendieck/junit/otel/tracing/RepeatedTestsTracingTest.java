package com.nikolasgrottendieck.junit.otel.tracing;

import com.nikolasgrottendieck.junit.otel.OpenTelemetryTracing;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.logs.Logger;
import io.opentelemetry.api.metrics.Meter;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.sdk.testing.junit5.OpenTelemetryExtension;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.engine.descriptor.JupiterEngineDescriptor;
import org.junit.platform.testkit.engine.EngineTestKit;

import static com.nikolasgrottendieck.junit.otel.SemConName.CLASS;
import static com.nikolasgrottendieck.junit.otel.SemConName.LIFECYCLE;
import static com.nikolasgrottendieck.junit.otel.SemConName.UNIQUE_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.MapAssert.assertThatMap;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

public class RepeatedTestsTracingTest {

	@RegisterExtension
	private static final OpenTelemetryExtension otelTesting = OpenTelemetryExtension.create();

	private final Tracer tracer = otelTesting.getOpenTelemetry().getTracer("test");
	private final Meter meter = otelTesting.getOpenTelemetry().getMeter("test");
	private final Logger logger = otelTesting.getOpenTelemetry().getLogsBridge().get("test");

	@Test
	void verifySpans() {
		EngineTestKit
			.engine(JupiterEngineDescriptor.ENGINE_ID)
			.selectors(selectClass(TracingRepeatedExampleTest.class))
			.execute()
			.testEvents()
			.assertStatistics(stats ->
				stats.started(2)
					.succeeded(2)
			);

		assertThat(otelTesting.getSpans())
			.satisfiesExactly(
				spanData -> {
					assertThat(spanData.getName()).isEqualTo("RepeatedTestsTracingTest$TracingRepeatedExampleTest");
					assertThatMap(spanData.getAttributes().asMap())
						.containsKeys(
							AttributeKey.stringKey(CLASS.getOtelName()),
							AttributeKey.stringKey(LIFECYCLE.getOtelName()),
							AttributeKey.stringKey(UNIQUE_ID.getOtelName())
						)
					;
				},
				spanData -> {
					assertThat(spanData.getName()).isEqualTo("repetition 1 of 2");
					assertThatMap(spanData.getAttributes().asMap())
						.containsKeys(
							AttributeKey.stringKey(CLASS.getOtelName()),
							AttributeKey.stringKey(LIFECYCLE.getOtelName()),
							AttributeKey.stringKey(UNIQUE_ID.getOtelName())
						)
					;
				},
				spanData -> {
					assertThat(spanData.getName()).isEqualTo("repetition 1 of 2");
					assertThatMap(spanData.getAttributes().asMap())
						.containsKeys(
							AttributeKey.stringKey(CLASS.getOtelName()),
							AttributeKey.stringKey(LIFECYCLE.getOtelName()),
							AttributeKey.stringKey(UNIQUE_ID.getOtelName())
						)
					;
				},
				spanData -> {
					assertThat(spanData.getName()).isEqualTo("RepeatedTestsTracingTest$TracingRepeatedExampleTest");
					assertThatMap(spanData.getAttributes().asMap())
						.containsKeys(
							AttributeKey.stringKey(CLASS.getOtelName()),
							AttributeKey.stringKey(LIFECYCLE.getOtelName()),
							AttributeKey.stringKey(UNIQUE_ID.getOtelName())
						)
					;
				},
				spanData -> {
					assertThat(spanData.getName()).isEqualTo("repetition 2 of 2");
					assertThatMap(spanData.getAttributes().asMap())
						.containsKeys(
							AttributeKey.stringKey(CLASS.getOtelName()),
							AttributeKey.stringKey(LIFECYCLE.getOtelName()),
							AttributeKey.stringKey(UNIQUE_ID.getOtelName())
						)
					;
				},
				spanData -> {
					assertThat(spanData.getName()).isEqualTo("repetition 2 of 2");
					assertThatMap(spanData.getAttributes().asMap())
						.containsKeys(
							AttributeKey.stringKey(CLASS.getOtelName()),
							AttributeKey.stringKey(LIFECYCLE.getOtelName()),
							AttributeKey.stringKey(UNIQUE_ID.getOtelName())
						)
					;
				},
				spanData -> {
					assertThat(spanData.getName()).isEqualTo("RepeatedTestsTracingTest$TracingRepeatedExampleTest");
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
	static class TracingRepeatedExampleTest {

		@RepeatedTest(2)
		void succeedingTest() {
			assertTrue(true);
		}
	}

}
