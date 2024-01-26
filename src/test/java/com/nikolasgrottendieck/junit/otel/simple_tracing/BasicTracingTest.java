package com.nikolasgrottendieck.junit.otel.simple_tracing;

import com.nikolasgrottendieck.junit.otel.OpenTelemetrySimpleTracing;
import com.nikolasgrottendieck.junit.otel.TestResult;
import com.nikolasgrottendieck.junit.otel.helper.TestCase;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.sdk.testing.junit5.OpenTelemetryExtension;
import io.opentelemetry.sdk.trace.data.StatusData;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;

import static com.nikolasgrottendieck.junit.otel.SemConName.CLASS;
import static com.nikolasgrottendieck.junit.otel.SemConName.LIFECYCLE;
import static com.nikolasgrottendieck.junit.otel.SemConName.RESULT;
import static com.nikolasgrottendieck.junit.otel.SemConName.RESULT_REASON;
import static com.nikolasgrottendieck.junit.otel.SemConName.UNIQUE_ID;
import static com.nikolasgrottendieck.junit.otel.TestLifecycle.PRE_INSTANCE_CONSTRUCT;
import static com.nikolasgrottendieck.junit.otel.TestLifecycle.TEST_EXECUTION;
import static com.nikolasgrottendieck.junit.otel.helper.TestCaseHelpers.engineTestKit;
import static com.nikolasgrottendieck.junit.otel.helper.TestCaseHelpers.uniqueId;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

class BasicTracingTest {

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
					.started(3)
					.succeeded(1)
			);

		otelTesting.assertTraces().hasTracesSatisfyingExactly(
			trace ->
				trace
					.hasSpansSatisfyingExactly(
						s -> s.hasName("BasicTracingTest$TracingExampleTestCase")
							.hasNoParent()
							.hasAttribute(
								AttributeKey.stringKey(UNIQUE_ID.getOtelName()),
								uniqueId(BasicTracingTest.TracingExampleTestCase.class))
							.hasAttribute(
								AttributeKey.stringKey(CLASS.getOtelName()),
								BasicTracingTest.TracingExampleTestCase.class.getCanonicalName()
							)
							.hasAttribute(
								AttributeKey.stringKey(LIFECYCLE.getOtelName()),
								PRE_INSTANCE_CONSTRUCT.toString()
							),
						s -> s.hasName("succeedingTest()")
							.hasParentSpanId(trace.getSpan(0).getSpanId())
							.hasParent(trace.getSpan(0))
							.hasStatus(StatusData.ok())
							.hasAttribute(
								AttributeKey.stringKey(UNIQUE_ID.getOtelName()),
								uniqueId(BasicTracingTest.TracingExampleTestCase.class, "succeedingTest()"))
							.hasAttribute(
								AttributeKey.stringKey(CLASS.getOtelName()),
								BasicTracingTest.TracingExampleTestCase.class.getCanonicalName()
							)
							.hasAttribute(
								AttributeKey.stringKey(LIFECYCLE.getOtelName()),
								TEST_EXECUTION.toString()
							)
							.hasAttribute(
								AttributeKey.stringKey(RESULT.getOtelName()),
								TestResult.SUCCESSFUL.name()
							),
						s -> s.hasName("failingTest()")
							.hasParentSpanId(trace.getSpan(0).getSpanId())
							.hasParent(trace.getSpan(0))
							.hasStatus(StatusData.error())
							.hasAttribute(
								AttributeKey.stringKey(UNIQUE_ID.getOtelName()),
								uniqueId(BasicTracingTest.TracingExampleTestCase.class, "failingTest()"))
							.hasAttribute(
								AttributeKey.stringKey(CLASS.getOtelName()),
								BasicTracingTest.TracingExampleTestCase.class.getCanonicalName()
							)
							.hasAttribute(
								AttributeKey.stringKey(LIFECYCLE.getOtelName()),
								TEST_EXECUTION.toString()
							)
							.hasAttribute(
								AttributeKey.stringKey(RESULT.getOtelName()),
								TestResult.FAILED.name()
							)
							.hasAttribute(
								AttributeKey.stringKey(RESULT_REASON.getOtelName()),
								"failed on purpose"
							),
						s -> s.hasName("abortedTest()")
							.hasParentSpanId(trace.getSpan(0).getSpanId())
							.hasParent(trace.getSpan(0))
							.hasAttribute(
								AttributeKey.stringKey(UNIQUE_ID.getOtelName()),
								uniqueId(BasicTracingTest.TracingExampleTestCase.class, "abortedTest()"))
							.hasAttribute(
								AttributeKey.stringKey(CLASS.getOtelName()),
								BasicTracingTest.TracingExampleTestCase.class.getCanonicalName()
							)
							.hasAttribute(
								AttributeKey.stringKey(LIFECYCLE.getOtelName()),
								TEST_EXECUTION.toString()
							)
							.hasAttribute(
								AttributeKey.stringKey(RESULT.getOtelName()),
								TestResult.ABORTED.name()
							)
							.hasAttribute(
								AttributeKey.stringKey(RESULT_REASON.getOtelName()),
								"Assumption failed: abc does not contain Z"
							)
					)
					.first()
					.hasName("BasicTracingTest$TracingExampleTestCase")
		);
	}

	@TestCase
	@ExtendWith(OpenTelemetrySimpleTracing.class)
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
