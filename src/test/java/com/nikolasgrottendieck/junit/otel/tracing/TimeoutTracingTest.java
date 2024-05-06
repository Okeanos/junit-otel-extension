package com.nikolasgrottendieck.junit.otel.tracing;

import com.nikolasgrottendieck.helper.TestCase;
import com.nikolasgrottendieck.junit.otel.OpenTelemetryTracing;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.sdk.testing.junit5.OpenTelemetryExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.concurrent.TimeUnit;

import static com.nikolasgrottendieck.helper.TestCaseHelpers.engineTestKit;
import static com.nikolasgrottendieck.helper.TestCaseHelpers.uniqueId;
import static java.lang.Thread.sleep;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

class TimeoutTracingTest {

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
					.failed(2)
					.started(2)
			);

		// TODO assertions here are incomplete and don't cover all expected spans & traces
		otelTesting.assertTraces().hasTracesSatisfyingExactly(
			trace ->
				trace.hasSpansSatisfyingExactlyInAnyOrder(
					s -> s.hasName("TimeoutTracingTest$TracingExampleTestCase")
						.hasNoParent()
						.hasAttribute(
							AttributeKey.stringKey("org.junit.test.unique_id"),
							uniqueId(TimeoutTracingTest.TracingExampleTestCase.class))
						.hasAttribute(
							AttributeKey.stringKey("org.junit.test.class"),
							TimeoutTracingTest.TracingExampleTestCase.class.getCanonicalName()
						)
						.hasAttribute(
							AttributeKey.stringKey("org.junit.test.lifecycle"),
							"PRE_INSTANCE_CONSTRUCT"
						),
					s -> s.hasName("timeoutTest()")
				),
			trace -> trace.hasSpansSatisfyingExactlyInAnyOrder(
				s -> s.hasName("TimeoutTracingTest$TracingExampleTestCase")
					.hasNoParent()
					.hasAttribute(
						AttributeKey.stringKey("org.junit.test.unique_id"),
						uniqueId(TimeoutTracingTest.TracingExampleTestCase.class))
					.hasAttribute(
						AttributeKey.stringKey("org.junit.test.class"),
						TimeoutTracingTest.TracingExampleTestCase.class.getCanonicalName()
					)
					.hasAttribute(
						AttributeKey.stringKey("org.junit.test.lifecycle"),
						"PRE_INSTANCE_CONSTRUCT"
					),
				s -> s.hasName("timeoutInSeparateThread()")
			)
		);
	}

	@TestCase
	@ExtendWith(OpenTelemetryTracing.class)
	static class TracingExampleTestCase {

		@Test
		@Timeout(value = 500, unit = TimeUnit.MILLISECONDS)
		void timeoutTest() throws InterruptedException {
			sleep(600);
		}

		@Test
		@Timeout(value = 500, unit = TimeUnit.MILLISECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
		void timeoutInSeparateThread() throws InterruptedException {
			sleep(600);
		}

	}

}
