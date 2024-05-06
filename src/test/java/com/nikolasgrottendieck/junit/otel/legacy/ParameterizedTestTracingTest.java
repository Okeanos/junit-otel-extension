package com.nikolasgrottendieck.junit.otel.legacy;

import com.nikolasgrottendieck.helper.TestCase;
import com.nikolasgrottendieck.junit.otel.OpenTelemetryLegacyTracing;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.logs.Logger;
import io.opentelemetry.api.metrics.Meter;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.sdk.testing.junit5.OpenTelemetryExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.engine.descriptor.JupiterEngineDescriptor;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.platform.testkit.engine.EngineTestKit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assumptions.assumeThat;
import static org.assertj.core.api.MapAssert.assertThatMap;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

@Deprecated
public class ParameterizedTestTracingTest {

	@RegisterExtension
	private static final OpenTelemetryExtension otelTesting = OpenTelemetryExtension.create();

	private final Tracer tracer = otelTesting.getOpenTelemetry().getTracer("test");
	private final Meter meter = otelTesting.getOpenTelemetry().getMeter("test");
	private final Logger logger = otelTesting.getOpenTelemetry().getLogsBridge().get("test");

	@Test
	void verifySpans() {
		EngineTestKit
			.engine(JupiterEngineDescriptor.ENGINE_ID)
			.selectors(selectClass(TracingParameterizedExampleTest.class))
			.execute()
			.testEvents()
			.assertStatistics(stats ->
				stats.started(3)
					.aborted(1)
			);

		assertThat(otelTesting.getSpans())
			.satisfiesExactly(
				spanData -> {
					assertThat(spanData.getName()).isEqualTo("ParameterizedTestTracingTest$TracingParameterizedExampleTest");
					assertThatMap(spanData.getAttributes().asMap())
						.containsKeys(
							AttributeKey.stringKey("org.junit.test.class"),
							AttributeKey.stringKey("org.junit.test.lifecycle"),
							AttributeKey.stringKey("org.junit.test.unique_id")
						)
					;
				},
				spanData -> {
					assertThat(spanData.getName()).isEqualTo("[1] 1");
					assertThatMap(spanData.getAttributes().asMap())
						.containsKeys(
							AttributeKey.stringKey("org.junit.test.class"),
							AttributeKey.stringKey("org.junit.test.lifecycle"),
							AttributeKey.stringKey("org.junit.test.unique_id")
						)
					;
				},
				spanData -> {
					assertThat(spanData.getName()).isEqualTo("[1] 1");
					assertThatMap(spanData.getAttributes().asMap())
						.containsKeys(
							AttributeKey.stringKey("org.junit.test.class"),
							AttributeKey.stringKey("org.junit.test.lifecycle"),
							AttributeKey.stringKey("org.junit.test.unique_id")
						)
					;
				},
				spanData -> {
					assertThat(spanData.getName()).isEqualTo("ParameterizedTestTracingTest$TracingParameterizedExampleTest");
					assertThatMap(spanData.getAttributes().asMap())
						.containsKeys(
							AttributeKey.stringKey("org.junit.test.class"),
							AttributeKey.stringKey("org.junit.test.lifecycle"),
							AttributeKey.stringKey("org.junit.test.unique_id")
						)
					;
				},
				spanData -> {
					assertThat(spanData.getName()).isEqualTo("[2] 2");
					assertThatMap(spanData.getAttributes().asMap())
						.containsKeys(
							AttributeKey.stringKey("org.junit.test.class"),
							AttributeKey.stringKey("org.junit.test.lifecycle"),
							AttributeKey.stringKey("org.junit.test.unique_id")
						)
					;
				},
				spanData -> {
					assertThat(spanData.getName()).isEqualTo("[2] 2");
					assertThatMap(spanData.getAttributes().asMap())
						.containsKeys(
							AttributeKey.stringKey("org.junit.test.class"),
							AttributeKey.stringKey("org.junit.test.lifecycle"),
							AttributeKey.stringKey("org.junit.test.unique_id")
						)
					;
				},
				spanData -> {
					assertThat(spanData.getName()).isEqualTo("ParameterizedTestTracingTest$TracingParameterizedExampleTest");
					assertThatMap(spanData.getAttributes().asMap())
						.containsKeys(
							AttributeKey.stringKey("org.junit.test.class"),
							AttributeKey.stringKey("org.junit.test.lifecycle"),
							AttributeKey.stringKey("org.junit.test.unique_id")
						)
					;
				},
				spanData -> {
					assertThat(spanData.getName()).isEqualTo("[3] 3");
					assertThatMap(spanData.getAttributes().asMap())
						.containsKeys(
							AttributeKey.stringKey("org.junit.test.class"),
							AttributeKey.stringKey("org.junit.test.lifecycle"),
							AttributeKey.stringKey("org.junit.test.unique_id")
						)
					;
				},
				spanData -> {
					assertThat(spanData.getName()).isEqualTo("[3] 3");
					assertThatMap(spanData.getAttributes().asMap())
						.containsKeys(
							AttributeKey.stringKey("org.junit.test.class"),
							AttributeKey.stringKey("org.junit.test.lifecycle"),
							AttributeKey.stringKey("org.junit.test.unique_id")
						)
					;
				},
				spanData -> {
					assertThat(spanData.getName()).isEqualTo("ParameterizedTestTracingTest$TracingParameterizedExampleTest");
					assertThatMap(spanData.getAttributes().asMap())
						.containsKeys(
							AttributeKey.stringKey("org.junit.test.class"),
							AttributeKey.stringKey("org.junit.test.lifecycle"),
							AttributeKey.stringKey("org.junit.test.unique_id")
						)
					;
				}
			);
	}

	@TestCase
	@ExtendWith(OpenTelemetryLegacyTracing.class)
	static class TracingParameterizedExampleTest {

		@ParameterizedTest
		@ValueSource(ints = {1, 2, 3})
		void testParameter(int param) {
			assertThat(param).isGreaterThan(1);
			assumeThat(param).isLessThan(3);
		}
	}

}
