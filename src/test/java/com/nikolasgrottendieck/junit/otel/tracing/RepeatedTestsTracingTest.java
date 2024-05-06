package com.nikolasgrottendieck.junit.otel.tracing;

import com.nikolasgrottendieck.helper.TestCase;
import com.nikolasgrottendieck.junit.otel.OpenTelemetryTracing;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.sdk.testing.junit5.OpenTelemetryExtension;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;

import static com.nikolasgrottendieck.helper.TestCaseHelpers.engineTestKit;
import static com.nikolasgrottendieck.helper.TestCaseHelpers.uniqueId;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

class RepeatedTestsTracingTest {

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
					.started(2)
					.dynamicallyRegistered(2)
					.succeeded(2)
			);

		// TODO assertions here are incomplete and don't cover all expected spans & traces
		otelTesting.assertTraces().hasTracesSatisfyingExactly(
			trace ->
				trace.hasSpansSatisfyingExactlyInAnyOrder(
					s -> s.hasName("RepeatedTestsTracingTest$TracingExampleTestCase")
						.hasNoParent()
						.hasAttribute(
							AttributeKey.stringKey("org.junit.test.unique_id"),
							uniqueId(RepeatedTestsTracingTest.TracingExampleTestCase.class))
						.hasAttribute(
							AttributeKey.stringKey("org.junit.test.class"),
							RepeatedTestsTracingTest.TracingExampleTestCase.class.getCanonicalName()
						)
						.hasAttribute(
							AttributeKey.stringKey("org.junit.test.lifecycle"),
							"PRE_INSTANCE_CONSTRUCT"
						)
				),
			trace ->
				trace.hasSpansSatisfyingExactlyInAnyOrder(
					s -> s.hasName("RepeatedTestsTracingTest$TracingExampleTestCase")
						.hasNoParent()
						.hasAttribute(
							AttributeKey.stringKey("org.junit.test.unique_id"),
							uniqueId(RepeatedTestsTracingTest.TracingExampleTestCase.class))
						.hasAttribute(
							AttributeKey.stringKey("org.junit.test.class"),
							RepeatedTestsTracingTest.TracingExampleTestCase.class.getCanonicalName()
						)
						.hasAttribute(
							AttributeKey.stringKey("org.junit.test.lifecycle"),
							"PRE_INSTANCE_CONSTRUCT"
						)
				)
		);
	}

	@TestCase
	@ExtendWith(OpenTelemetryTracing.class)
	static class TracingExampleTestCase {

		@RepeatedTest(2)
		void succeedingTest() {
			assertTrue(true);
		}
	}

}
