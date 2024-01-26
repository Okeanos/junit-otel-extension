package com.nikolasgrottendieck.junit.otel.tracing;

import com.nikolasgrottendieck.junit.otel.OpenTelemetryTracing;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.logs.Logger;
import io.opentelemetry.api.metrics.Meter;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.sdk.testing.junit5.OpenTelemetryExtension;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.engine.descriptor.JupiterEngineDescriptor;
import org.junit.platform.testkit.engine.EngineTestKit;

import static com.nikolasgrottendieck.junit.otel.SemConName.CLASS;
import static com.nikolasgrottendieck.junit.otel.SemConName.LIFECYCLE;
import static com.nikolasgrottendieck.junit.otel.SemConName.METHOD;
import static com.nikolasgrottendieck.junit.otel.SemConName.RESULT;
import static com.nikolasgrottendieck.junit.otel.SemConName.RESULT_REASON;
import static com.nikolasgrottendieck.junit.otel.SemConName.UNIQUE_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.MapAssert.assertThatMap;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

public class BasicTracingTest {

	@RegisterExtension
	private static final OpenTelemetryExtension otelTesting = OpenTelemetryExtension.create();

	private final Tracer tracer = otelTesting.getOpenTelemetry().getTracer("test");
	private final Meter meter = otelTesting.getOpenTelemetry().getMeter("test");
	private final Logger logger = otelTesting.getOpenTelemetry().getLogsBridge().get("test");

	@Test
	void verifySpans() {
		EngineTestKit
			.engine(JupiterEngineDescriptor.ENGINE_ID)
			.selectors(selectClass(TracingExampleTest.class))
			.execute()
			.testEvents()
			.assertStatistics(stats ->
				stats.skipped(1)
					.started(3)
					.succeeded(1)
					.aborted(1)
					.failed(1)
			);

		assertThat(otelTesting.getSpans())
			.satisfiesExactly(
				spanData -> {
					assertThat(spanData.getName()).isEqualTo("BasicTracingTest$TracingExampleTest");
					assertThatMap(spanData.getAttributes().asMap())
						.containsKeys(
							AttributeKey.stringKey(CLASS.getOtelName()),
							AttributeKey.stringKey(LIFECYCLE.getOtelName()),
							AttributeKey.stringKey(UNIQUE_ID.getOtelName())
						)
					;
				},
				spanData -> {
					assertThat(spanData.getName()).isEqualTo("BasicTracingTest$TracingExampleTest");
					assertThatMap(spanData.getAttributes().asMap())
						.containsKeys(
							AttributeKey.stringKey(CLASS.getOtelName()),
							AttributeKey.stringKey(LIFECYCLE.getOtelName()),
							AttributeKey.stringKey(UNIQUE_ID.getOtelName())
						)
					;
				},
				spanData -> {
					assertThat(spanData.getName()).isEqualTo("succeedingTest()");
					assertThatMap(spanData.getAttributes().asMap())
						.containsKeys(
							AttributeKey.stringKey(CLASS.getOtelName()),
							AttributeKey.stringKey(LIFECYCLE.getOtelName()),
							AttributeKey.stringKey(UNIQUE_ID.getOtelName()),
							AttributeKey.stringKey(METHOD.getOtelName()),
							AttributeKey.stringKey(RESULT.getOtelName())
						)
					;
				},
				spanData -> {
					assertThat(spanData.getName()).isEqualTo("succeedingTest()");
					assertThatMap(spanData.getAttributes().asMap())
						.containsKeys(
							AttributeKey.stringKey(CLASS.getOtelName()),
							AttributeKey.stringKey(LIFECYCLE.getOtelName()),
							AttributeKey.stringKey(UNIQUE_ID.getOtelName()),
							AttributeKey.stringKey(METHOD.getOtelName())
						)
					;
				},
				spanData -> {
					assertThat(spanData.getName()).isEqualTo("BasicTracingTest$TracingExampleTest");
					assertThatMap(spanData.getAttributes().asMap())
						.containsKeys(
							AttributeKey.stringKey(CLASS.getOtelName()),
							AttributeKey.stringKey(LIFECYCLE.getOtelName()),
							AttributeKey.stringKey(UNIQUE_ID.getOtelName())
						)
					;
				},
				spanData -> {
					assertThat(spanData.getName()).isEqualTo("abortedTest()");
					assertThatMap(spanData.getAttributes().asMap())
						.containsKeys(
							AttributeKey.stringKey(CLASS.getOtelName()),
							AttributeKey.stringKey(LIFECYCLE.getOtelName()),
							AttributeKey.stringKey(UNIQUE_ID.getOtelName()),
							AttributeKey.stringKey(METHOD.getOtelName()),
							AttributeKey.stringKey(RESULT.getOtelName()),
							AttributeKey.stringKey(RESULT_REASON.getOtelName())
						)
					;
				},
				spanData -> {
					assertThat(spanData.getName()).isEqualTo("abortedTest()");
					assertThatMap(spanData.getAttributes().asMap())
						.containsKeys(
							AttributeKey.stringKey(CLASS.getOtelName()),
							AttributeKey.stringKey(LIFECYCLE.getOtelName()),
							AttributeKey.stringKey(UNIQUE_ID.getOtelName()),
							AttributeKey.stringKey(METHOD.getOtelName())
						)
					;
				},
				spanData -> {
					assertThat(spanData.getName()).isEqualTo("BasicTracingTest$TracingExampleTest");
					assertThatMap(spanData.getAttributes().asMap())
						.containsKeys(
							AttributeKey.stringKey(CLASS.getOtelName()),
							AttributeKey.stringKey(LIFECYCLE.getOtelName()),
							AttributeKey.stringKey(UNIQUE_ID.getOtelName())
						)
					;
				},
				spanData -> {
					assertThat(spanData.getName()).isEqualTo("failingTest()");
					assertThatMap(spanData.getAttributes().asMap())
						.containsKeys(
							AttributeKey.stringKey(CLASS.getOtelName()),
							AttributeKey.stringKey(LIFECYCLE.getOtelName()),
							AttributeKey.stringKey(UNIQUE_ID.getOtelName()),
							AttributeKey.stringKey(METHOD.getOtelName()),
							AttributeKey.stringKey(RESULT.getOtelName()),
							AttributeKey.stringKey(RESULT_REASON.getOtelName())
						)
					;
				},
				spanData -> {
					assertThat(spanData.getName()).isEqualTo("failingTest()");
					assertThatMap(spanData.getAttributes().asMap())
						.containsKeys(
							AttributeKey.stringKey(CLASS.getOtelName()),
							AttributeKey.stringKey(LIFECYCLE.getOtelName()),
							AttributeKey.stringKey(UNIQUE_ID.getOtelName()),
							AttributeKey.stringKey(METHOD.getOtelName())
						)
					;
				},
				spanData -> {
					assertThat(spanData.getName()).isEqualTo("BasicTracingTest$TracingExampleTest");
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
	@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
	static class TracingExampleTest {

		@Test
		@Disabled("for demonstration purposes")
		@Order(1)
		void skippedTest() {
			// skipped ...
		}

		@Test
		@Order(2)
		void succeedingTest() {
			assertTrue(true);
		}

		@Test
		@Order(3)
		void abortedTest() {
			assumeTrue("abc".contains("Z"), "abc does not contain Z");
			// aborted ...
		}

		@Test
		@Order(4)
		void failingTest() {
			fail("failed on purpose");
		}
	}

}
