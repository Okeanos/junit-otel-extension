package com.nikolasgrottendieck.junit.otel.simple_tracing;

import com.nikolasgrottendieck.junit.otel.OpenTelemetrySimpleTracing;
import com.nikolasgrottendieck.junit.otel.helper.TestCase;
import io.opentelemetry.sdk.testing.junit5.OpenTelemetryExtension;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;

import static com.nikolasgrottendieck.junit.otel.helper.TestCaseHelpers.engineTestKit;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

class NamedTracingTest {

	@RegisterExtension
	private static final OpenTelemetryExtension otelTesting = OpenTelemetryExtension.create();

	@Test
	void verifySpans() {
		engineTestKit()
			.selectors(selectClass(TracingExampleTestCase.class))
			.execute()
			.testEvents()
			.assertStatistics(stats ->
				stats
					.aborted(1)
					.failed(1)
					.skipped(1)
					.started(4)
					.succeeded(2)
			);

		assertThat(otelTesting.getSpans())
			.hasSize(5)
//			.satisfiesExactly(spanData -> {
//					assertThat(spanData.getName()).isEqualTo("Fancy Tracing Example Test Class");
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
	@DisplayName("Fancy Tracing Example Test Class")
	static class TracingExampleTestCase {

		@Test
		@DisplayName("An aborted test")
		void abortedTest() {
			assumeTrue("abc".contains("Z"), "abc does not contain Z");
			// aborted ...
		}

		@Test
		@DisplayName("A failing test")
		void failingTest() {
			fail("failed on purpose");
		}

		@Test
		@Disabled("for demonstration purposes")
		@DisplayName("A skipped/disabled test")
		void skippedTest() {
			// skipped ...
		}

		@Test
		@DisplayName("A succeeding Test")
		void succeedingTest() {
			assertTrue(true);
		}

		@Test
		@DisplayName("✅ Has an Emoji Name")
		void emojiTest() {
			assertTrue(true);
		}
	}

}
