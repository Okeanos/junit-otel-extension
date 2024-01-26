package com.nikolasgrottendieck.junit.otel.simple_tracing;

import com.nikolasgrottendieck.junit.otel.OpenTelemetrySimpleTracing;
import com.nikolasgrottendieck.junit.otel.helper.TestCase;
import io.opentelemetry.sdk.testing.junit5.OpenTelemetryExtension;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.List;

import static com.nikolasgrottendieck.junit.otel.helper.TestCaseHelpers.engineTestKit;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectNestedClass;

class NestedTracingTest {

	@RegisterExtension
	private static final OpenTelemetryExtension otelTesting = OpenTelemetryExtension.create();

	@Test
	void verifySpans() {
		engineTestKit()
			.selectors(
				// TODO check whether selection is correct, would expect a single "do parent + nested" selector to exist
				selectClass(TracingExampleTestCase.class),
				selectNestedClass(List.of(TracingExampleTestCase.NestedTracingExampleTestCase.class), TracingExampleTestCase.class)
			)
			.execute()
			.containerEvents() // TODO check whether this is correct, would expect testEvents() to work as well
			.assertStatistics(stats ->
				stats
					.started(2)
					.succeeded(2)
			);

		assertThat(otelTesting.getSpans())
			.hasSize(2)
//			.satisfiesExactly(spanData -> {
//					assertThat(spanData.getName()).isEqualTo("NestedTracingTest$TracingExampleTestCase");
//					assertThat(spanData.getParentSpanContext().getTraceId()).isEqualTo("00000000000000000000000000000000");
//					assertThat(spanData.getParentSpanId()).isEqualTo("0000000000000000");
//
//					assertThat(spanData.getAttributes().asMap())
//						.contains(
//							entry(AttributeKey.stringKey(CLASS.getOtelName()), TracingExampleTestCase.class.getCanonicalName()),
//							entry(AttributeKey.stringKey(LIFECYCLE.getOtelName()), PRE_INSTANCE_CONSTRUCT.toString()),
//							entry(AttributeKey.stringKey(UNIQUE_ID.getOtelName()), uniqueId(TracingExampleTestCase.class))
//						);
//					assertThat(spanData.hasEnded()).isTrue();
//				}
//			)
		;
	}

	@TestCase
	@ExtendWith(OpenTelemetrySimpleTracing.class)
	static class TracingExampleTestCase {

		@Test
		void succeedingTest() {
			assertTrue(true);
		}

		@TestCase
		@Nested
		static class NestedTracingExampleTestCase {

			@Test
			void succeedingTest() {
				assertTrue(true);
			}

		}
	}

}
