package com.nikolasgrottendieck.junit.otel.tracing;

import com.nikolasgrottendieck.helper.TestCase;
import com.nikolasgrottendieck.junit.otel.OpenTelemetryTracing;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.sdk.testing.junit5.OpenTelemetryExtension;
import io.opentelemetry.sdk.trace.data.StatusData;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junitpioneer.jupiter.SetEnvironmentVariable;

import static com.nikolasgrottendieck.helper.TestCaseHelpers.engineTestKit;
import static com.nikolasgrottendieck.helper.TestCaseHelpers.uniqueId;
import static com.nikolasgrottendieck.junit.otel.tracing.OutsideContextTracingTest.outsideTraceId;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

// If not set here (but at the level of the nested TestCase instead), the environment variable is not available for OTEL
@SetEnvironmentVariable(key = "TRACEPARENT", value = outsideTraceId)
class OutsideContextTracingTest {

	@RegisterExtension
	private static final OpenTelemetryExtension otelTesting = OpenTelemetryExtension.create();

	static final String outsideTraceId = "00-a56c99412532db9f8b31cbd521f25072-3c613b0dd964f557-01";
	static final String outsideSpanId = "3c613b0dd964f557";

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
					.started(3)
					.succeeded(1)
			);

		otelTesting.assertTraces().hasTracesSatisfyingExactly(
			trace ->
				trace
					.hasSpansSatisfyingExactlyInAnyOrder(
						s -> s.hasName("OutsideContextTracingTest$TracingExampleTestCase")
							.hasParentSpanId(outsideSpanId)
							.hasAttribute(
								AttributeKey.stringKey("org.junit.test.unique_id"),
								uniqueId(OutsideContextTracingTest.TracingExampleTestCase.class))
							.hasAttribute(
								AttributeKey.stringKey("org.junit.test.class"),
								OutsideContextTracingTest.TracingExampleTestCase.class.getCanonicalName()
							)
							.hasAttribute(
								AttributeKey.stringKey("org.junit.test.lifecycle"),
								"PRE_INSTANCE_CONSTRUCT"
							),
						s -> s.hasName("succeedingTest()")
							.hasParentSpanId(trace.getSpan(0).getSpanId())
							.hasParent(trace.getSpan(0))
							.hasStatus(StatusData.ok())
							.hasAttribute(
								AttributeKey.stringKey("org.junit.test.unique_id"),
								uniqueId(OutsideContextTracingTest.TracingExampleTestCase.class, "succeedingTest()"))
							.hasAttribute(
								AttributeKey.stringKey("org.junit.test.class"),
								OutsideContextTracingTest.TracingExampleTestCase.class.getCanonicalName()
							)
							.hasAttribute(
								AttributeKey.stringKey("org.junit.test.lifecycle"),
								"TEST_EXECUTION"
							)
							.hasAttribute(
								AttributeKey.stringKey("org.junit.test.result"),
								"SUCCESSFUL"
							),
						s -> s.hasName("failingTest()")
							.hasParentSpanId(trace.getSpan(0).getSpanId())
							.hasParent(trace.getSpan(0))
							.hasStatus(StatusData.error())
							.hasAttribute(
								AttributeKey.stringKey("org.junit.test.unique_id"),
								uniqueId(OutsideContextTracingTest.TracingExampleTestCase.class, "failingTest()"))
							.hasAttribute(
								AttributeKey.stringKey("org.junit.test.class"),
								OutsideContextTracingTest.TracingExampleTestCase.class.getCanonicalName()
							)
							.hasAttribute(
								AttributeKey.stringKey("org.junit.test.lifecycle"),
								"TEST_EXECUTION"
							)
							.hasAttribute(
								AttributeKey.stringKey("org.junit.test.result"),
								"FAILED"
							)
							.hasAttribute(
								AttributeKey.stringKey("org.junit.test.result.reason"),
								"failed on purpose"
							),
						s -> s.hasName("abortedTest()")
							.hasParentSpanId(trace.getSpan(0).getSpanId())
							.hasParent(trace.getSpan(0))
							.hasAttribute(
								AttributeKey.stringKey("org.junit.test.unique_id"),
								uniqueId(OutsideContextTracingTest.TracingExampleTestCase.class, "abortedTest()"))
							.hasAttribute(
								AttributeKey.stringKey("org.junit.test.class"),
								OutsideContextTracingTest.TracingExampleTestCase.class.getCanonicalName()
							)
							.hasAttribute(
								AttributeKey.stringKey("org.junit.test.lifecycle"),
								"TEST_EXECUTION"
							)
							.hasAttribute(
								AttributeKey.stringKey("org.junit.test.result"),
								"ABORTED"
							)
							.hasAttribute(
								AttributeKey.stringKey("org.junit.test.result.reason"),
								"Assumption failed: abc does not contain Z"
							)
					)
					.first()
					.hasName("OutsideContextTracingTest$TracingExampleTestCase")
		);
	}

	@TestCase
	@ExtendWith(OpenTelemetryTracing.class)
	@TestInstance(TestInstance.Lifecycle.PER_CLASS)
	static class TracingExampleTestCase {

		@Test
		void abortedTest() {
			assumeTrue("abc".contains("Z"), "abc does not contain Z");
			// aborted ...
		}

		@Test
		void failingTest() {
			fail("failed on purpose");
		}

		@Test
		@Disabled("for demonstration purposes")
		void skippedTest() {
			// skipped ...
		}

		@Test
		void succeedingTest() {
			assertTrue(true);
		}
	}

}
