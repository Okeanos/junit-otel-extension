package com.nikolasgrottendieck.junit.otel.tracing;

import com.nikolasgrottendieck.helper.TestCase;
import com.nikolasgrottendieck.junit.otel.OpenTelemetryTracing;
import com.nikolasgrottendieck.junit.otel.SemConName;
import com.nikolasgrottendieck.junit.otel.TestLifecycle;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.sdk.testing.junit5.OpenTelemetryExtension;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;

import static com.nikolasgrottendieck.helper.TestCaseHelpers.engineTestKit;
import static com.nikolasgrottendieck.helper.TestCaseHelpers.uniqueId;
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

		// TODO assertions here are incomplete and don't cover all expected spans & traces
		otelTesting.assertTraces().hasTracesSatisfyingExactly(
			trace ->
				trace.hasSpansSatisfyingExactlyInAnyOrder(
					s -> s.hasName("Fancy Tracing Example Test Class")
						.hasNoParent()
						.hasAttribute(
							AttributeKey.stringKey(SemConName.UNIQUE_ID.getOtelName()),
							uniqueId(NamedTracingTest.TracingExampleTestCase.class))
						.hasAttribute(
							AttributeKey.stringKey(SemConName.CLASS.getOtelName()),
							NamedTracingTest.TracingExampleTestCase.class.getCanonicalName()
						)
						.hasAttribute(
							AttributeKey.stringKey(SemConName.LIFECYCLE.getOtelName()),
							TestLifecycle.PRE_INSTANCE_CONSTRUCT.name()
						),
					s -> s.hasName("A succeeding Test")
				),
			trace ->
				trace.hasSpansSatisfyingExactlyInAnyOrder(
					s -> s.hasName("Fancy Tracing Example Test Class")
						.hasNoParent()
						.hasAttribute(
							AttributeKey.stringKey(SemConName.UNIQUE_ID.getOtelName()),
							uniqueId(NamedTracingTest.TracingExampleTestCase.class))
						.hasAttribute(
							AttributeKey.stringKey(SemConName.CLASS.getOtelName()),
							NamedTracingTest.TracingExampleTestCase.class.getCanonicalName()
						)
						.hasAttribute(
							AttributeKey.stringKey(SemConName.LIFECYCLE.getOtelName()),
							TestLifecycle.PRE_INSTANCE_CONSTRUCT.name()
						),
					s -> s.hasName("A failing test")
				),
			trace ->
				trace.hasSpansSatisfyingExactlyInAnyOrder(
					s -> s.hasName("Fancy Tracing Example Test Class")
						.hasNoParent()
						.hasAttribute(
							AttributeKey.stringKey(SemConName.UNIQUE_ID.getOtelName()),
							uniqueId(NamedTracingTest.TracingExampleTestCase.class))
						.hasAttribute(
							AttributeKey.stringKey(SemConName.CLASS.getOtelName()),
							NamedTracingTest.TracingExampleTestCase.class.getCanonicalName()
						)
						.hasAttribute(
							AttributeKey.stringKey(SemConName.LIFECYCLE.getOtelName()),
							TestLifecycle.PRE_INSTANCE_CONSTRUCT.name()
						)
				),
			trace ->
				trace.hasSpansSatisfyingExactlyInAnyOrder(
					s -> s.hasName("Fancy Tracing Example Test Class")
						.hasNoParent()
						.hasAttribute(
							AttributeKey.stringKey(SemConName.UNIQUE_ID.getOtelName()),
							uniqueId(NamedTracingTest.TracingExampleTestCase.class))
						.hasAttribute(
							AttributeKey.stringKey(SemConName.CLASS.getOtelName()),
							NamedTracingTest.TracingExampleTestCase.class.getCanonicalName()
						)
						.hasAttribute(
							AttributeKey.stringKey(SemConName.LIFECYCLE.getOtelName()),
							TestLifecycle.PRE_INSTANCE_CONSTRUCT.name()
						),
					s -> s.hasName("An aborted test")
				),
			trace ->
				trace.hasSpansSatisfyingExactlyInAnyOrder(
					s -> s.hasName("Fancy Tracing Example Test Class")
						.hasNoParent()
						.hasAttribute(
							AttributeKey.stringKey(SemConName.UNIQUE_ID.getOtelName()),
							uniqueId(NamedTracingTest.TracingExampleTestCase.class))
						.hasAttribute(
							AttributeKey.stringKey(SemConName.CLASS.getOtelName()),
							NamedTracingTest.TracingExampleTestCase.class.getCanonicalName()
						)
						.hasAttribute(
							AttributeKey.stringKey(SemConName.LIFECYCLE.getOtelName()),
							TestLifecycle.PRE_INSTANCE_CONSTRUCT.name()
						),
					s -> s.hasName("✅ Has an Emoji Name")
				)
		);
	}

	@TestCase
	@ExtendWith(OpenTelemetryTracing.class)
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
