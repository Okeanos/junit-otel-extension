package com.nikolasgrottendieck.junit.otel.tracing;

import com.nikolasgrottendieck.helper.TestCase;
import com.nikolasgrottendieck.junit.otel.OpenTelemetryTracing;
import com.nikolasgrottendieck.junit.otel.SemConName;
import com.nikolasgrottendieck.junit.otel.TestLifecycle;
import com.nikolasgrottendieck.junit.otel.TestResult;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.sdk.testing.junit5.OpenTelemetryExtension;
import io.opentelemetry.sdk.trace.data.StatusData;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;

import static com.nikolasgrottendieck.helper.TestCaseHelpers.engineTestKit;
import static com.nikolasgrottendieck.helper.TestCaseHelpers.uniqueId;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

class BasicTracingTest {

	@RegisterExtension
	private static final OpenTelemetryExtension otelTesting = OpenTelemetryExtension.create();

	@Test
	void verifyPerClassSpans() {
		engineTestKit()
			.selectors(selectClass(TracingPerClassExampleTestCase.class))
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
						s -> s.hasName("BasicTracingTest$TracingPerClassExampleTestCase")
							.hasNoParent()
							.hasAttribute(
								AttributeKey.stringKey(SemConName.UNIQUE_ID.getOtelName()),
								uniqueId(BasicTracingTest.TracingPerClassExampleTestCase.class))
							.hasAttribute(
								AttributeKey.stringKey(SemConName.CLASS.getOtelName()),
								BasicTracingTest.TracingPerClassExampleTestCase.class.getCanonicalName()
							)
							.hasAttribute(
								AttributeKey.stringKey(SemConName.LIFECYCLE.getOtelName()),
								TestLifecycle.PRE_INSTANCE_CONSTRUCT.name()
							),
						s -> s.hasName("succeedingTest()")
							.hasParentSpanId(trace.getSpan(0).getSpanId())
							.hasParent(trace.getSpan(0))
							.hasStatus(StatusData.ok())
							.hasAttribute(
								AttributeKey.stringKey(SemConName.UNIQUE_ID.getOtelName()),
								uniqueId(BasicTracingTest.TracingPerClassExampleTestCase.class, "succeedingTest()"))
							.hasAttribute(
								AttributeKey.stringKey(SemConName.CLASS.getOtelName()),
								BasicTracingTest.TracingPerClassExampleTestCase.class.getCanonicalName()
							)
							.hasAttribute(
								AttributeKey.stringKey(SemConName.LIFECYCLE.getOtelName()),
								TestLifecycle.TEST_EXECUTION.name()
							)
							.hasAttribute(
								AttributeKey.stringKey(SemConName.RESULT.getOtelName()),
								TestResult.SUCCESSFUL.name()
							),
						s -> s.hasName("failingTest()")
							.hasParentSpanId(trace.getSpan(0).getSpanId())
							.hasParent(trace.getSpan(0))
							.hasStatus(StatusData.error())
							.hasAttribute(
								AttributeKey.stringKey(SemConName.UNIQUE_ID.getOtelName()),
								uniqueId(BasicTracingTest.TracingPerClassExampleTestCase.class, "failingTest()"))
							.hasAttribute(
								AttributeKey.stringKey(SemConName.CLASS.getOtelName()),
								BasicTracingTest.TracingPerClassExampleTestCase.class.getCanonicalName()
							)
							.hasAttribute(
								AttributeKey.stringKey(SemConName.LIFECYCLE.getOtelName()),
								TestLifecycle.TEST_EXECUTION.name()
							)
							.hasAttribute(
								AttributeKey.stringKey(SemConName.RESULT.getOtelName()),
								TestResult.FAILED.name()
							)
							.hasAttribute(
								AttributeKey.stringKey(SemConName.RESULT_REASON.getOtelName()),
								"failed on purpose"
							),
						s -> s.hasName("abortedTest()")
							.hasParentSpanId(trace.getSpan(0).getSpanId())
							.hasParent(trace.getSpan(0))
							.hasAttribute(
								AttributeKey.stringKey(SemConName.UNIQUE_ID.getOtelName()),
								uniqueId(BasicTracingTest.TracingPerClassExampleTestCase.class, "abortedTest()"))
							.hasAttribute(
								AttributeKey.stringKey(SemConName.CLASS.getOtelName()),
								BasicTracingTest.TracingPerClassExampleTestCase.class.getCanonicalName()
							)
							.hasAttribute(
								AttributeKey.stringKey(SemConName.LIFECYCLE.getOtelName()),
								TestLifecycle.TEST_EXECUTION.name()
							)
							.hasAttribute(
								AttributeKey.stringKey(SemConName.RESULT.getOtelName()),
								TestResult.ABORTED.name()
							)
							.hasAttribute(
								AttributeKey.stringKey(SemConName.RESULT_REASON.getOtelName()),
								"Assumption failed: abc does not contain Z"
							)
					)
					.first()
					.hasName("BasicTracingTest$TracingPerClassExampleTestCase")
		);
	}

	@Test
	void verifyPerMethodSpans() {
		engineTestKit()
			.selectors(selectClass(TracingPerMethodExampleTestCase.class))
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
						s -> s.hasName("BasicTracingTest$TracingPerMethodExampleTestCase")
							.hasNoParent()
							.hasAttribute(
								AttributeKey.stringKey(SemConName.UNIQUE_ID.getOtelName()),
								uniqueId(BasicTracingTest.TracingPerMethodExampleTestCase.class))
							.hasAttribute(
								AttributeKey.stringKey(SemConName.CLASS.getOtelName()),
								BasicTracingTest.TracingPerMethodExampleTestCase.class.getCanonicalName()
							)
							.hasAttribute(
								AttributeKey.stringKey(SemConName.LIFECYCLE.getOtelName()),
								TestLifecycle.PRE_INSTANCE_CONSTRUCT.name()
							),
						s -> s.hasName("succeedingTest()")
							.hasParentSpanId(trace.getSpan(0).getSpanId())
							.hasParent(trace.getSpan(0))
							.hasStatus(StatusData.ok())
							.hasAttribute(
								AttributeKey.stringKey(SemConName.UNIQUE_ID.getOtelName()),
								uniqueId(BasicTracingTest.TracingPerMethodExampleTestCase.class, "succeedingTest()"))
							.hasAttribute(
								AttributeKey.stringKey(SemConName.CLASS.getOtelName()),
								BasicTracingTest.TracingPerMethodExampleTestCase.class.getCanonicalName()
							)
							.hasAttribute(
								AttributeKey.stringKey(SemConName.LIFECYCLE.getOtelName()),
								TestLifecycle.TEST_EXECUTION.name()
							)
							.hasAttribute(
								AttributeKey.stringKey(SemConName.RESULT.getOtelName()),
								TestResult.SUCCESSFUL.name()
							)
					)
					.first()
					.hasName("BasicTracingTest$TracingPerMethodExampleTestCase"),
			trace ->
				trace.hasSpansSatisfyingExactlyInAnyOrder(
						s -> s.hasName("BasicTracingTest$TracingPerMethodExampleTestCase")
							.hasNoParent()
							.hasAttribute(
								AttributeKey.stringKey(SemConName.UNIQUE_ID.getOtelName()),
								uniqueId(BasicTracingTest.TracingPerMethodExampleTestCase.class))
							.hasAttribute(
								AttributeKey.stringKey(SemConName.CLASS.getOtelName()),
								BasicTracingTest.TracingPerMethodExampleTestCase.class.getCanonicalName()
							)
							.hasAttribute(
								AttributeKey.stringKey(SemConName.LIFECYCLE.getOtelName()),
								TestLifecycle.PRE_INSTANCE_CONSTRUCT.name()
							),
						s -> s.hasName("failingTest()")
							.hasParentSpanId(trace.getSpan(0).getSpanId())
							.hasParent(trace.getSpan(0))
							.hasStatus(StatusData.error())
							.hasAttribute(
								AttributeKey.stringKey(SemConName.UNIQUE_ID.getOtelName()),
								uniqueId(BasicTracingTest.TracingPerMethodExampleTestCase.class, "failingTest()"))
							.hasAttribute(
								AttributeKey.stringKey(SemConName.CLASS.getOtelName()),
								BasicTracingTest.TracingPerMethodExampleTestCase.class.getCanonicalName()
							)
							.hasAttribute(
								AttributeKey.stringKey(SemConName.LIFECYCLE.getOtelName()),
								TestLifecycle.TEST_EXECUTION.name()
							)
							.hasAttribute(
								AttributeKey.stringKey(SemConName.RESULT.getOtelName()),
								TestResult.FAILED.name()
							)
							.hasAttribute(
								AttributeKey.stringKey(SemConName.RESULT_REASON.getOtelName()),
								"failed on purpose"
							)
					)
					.first()
					.hasName("BasicTracingTest$TracingPerMethodExampleTestCase"),
			trace -> // the skipped trace does not create a test execution span but does create a pre_instance_construct one
				trace.hasSpansSatisfyingExactlyInAnyOrder(
						s -> s.hasName("BasicTracingTest$TracingPerMethodExampleTestCase")
							.hasNoParent()
							.hasAttribute(
								AttributeKey.stringKey(SemConName.UNIQUE_ID.getOtelName()),
								uniqueId(BasicTracingTest.TracingPerMethodExampleTestCase.class))
							.hasAttribute(
								AttributeKey.stringKey(SemConName.CLASS.getOtelName()),
								BasicTracingTest.TracingPerMethodExampleTestCase.class.getCanonicalName()
							)
							.hasAttribute(
								AttributeKey.stringKey(SemConName.LIFECYCLE.getOtelName()),
								TestLifecycle.PRE_INSTANCE_CONSTRUCT.name()
							)
					)
					.first()
					.hasName("BasicTracingTest$TracingPerMethodExampleTestCase"),
			trace ->
				trace.hasSpansSatisfyingExactlyInAnyOrder(
						s -> s.hasName("BasicTracingTest$TracingPerMethodExampleTestCase")
							.hasNoParent()
							.hasAttribute(
								AttributeKey.stringKey(SemConName.UNIQUE_ID.getOtelName()),
								uniqueId(BasicTracingTest.TracingPerMethodExampleTestCase.class))
							.hasAttribute(
								AttributeKey.stringKey(SemConName.CLASS.getOtelName()),
								BasicTracingTest.TracingPerMethodExampleTestCase.class.getCanonicalName()
							)
							.hasAttribute(
								AttributeKey.stringKey(SemConName.LIFECYCLE.getOtelName()),
								TestLifecycle.PRE_INSTANCE_CONSTRUCT.name()
							),
						s -> s.hasName("abortedTest()")
							.hasParentSpanId(trace.getSpan(0).getSpanId())
							.hasParent(trace.getSpan(0))
							.hasAttribute(
								AttributeKey.stringKey(SemConName.UNIQUE_ID.getOtelName()),
								uniqueId(BasicTracingTest.TracingPerMethodExampleTestCase.class, "abortedTest()"))
							.hasAttribute(
								AttributeKey.stringKey(SemConName.CLASS.getOtelName()),
								BasicTracingTest.TracingPerMethodExampleTestCase.class.getCanonicalName()
							)
							.hasAttribute(
								AttributeKey.stringKey(SemConName.LIFECYCLE.getOtelName()),
								TestLifecycle.TEST_EXECUTION.name()
							)
							.hasAttribute(
								AttributeKey.stringKey(SemConName.RESULT.getOtelName()),
								TestResult.ABORTED.name()
							)
							.hasAttribute(
								AttributeKey.stringKey(SemConName.RESULT_REASON.getOtelName()),
								"Assumption failed: abc does not contain Z"
							)
					)
					.first()
					.hasName("BasicTracingTest$TracingPerMethodExampleTestCase")
		);
	}

	@TestCase
	@ExtendWith(OpenTelemetryTracing.class)
	@TestInstance(TestInstance.Lifecycle.PER_CLASS)
	static class TracingPerClassExampleTestCase {

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

	@TestCase
	@ExtendWith(OpenTelemetryTracing.class)
	@TestInstance(TestInstance.Lifecycle.PER_METHOD)
	static class TracingPerMethodExampleTestCase {

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
