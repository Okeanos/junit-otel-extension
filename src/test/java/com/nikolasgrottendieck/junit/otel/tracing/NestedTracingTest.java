package com.nikolasgrottendieck.junit.otel.tracing;

import com.nikolasgrottendieck.helper.TestCase;
import com.nikolasgrottendieck.junit.otel.OpenTelemetryTracing;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.sdk.testing.junit5.OpenTelemetryExtension;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.List;

import static com.nikolasgrottendieck.helper.TestCaseHelpers.engineTestKit;
import static com.nikolasgrottendieck.helper.TestCaseHelpers.uniqueId;
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

		// TODO assertions here are incomplete and don't cover all expected spans & traces
		otelTesting.assertTraces().hasTracesSatisfyingExactly(
			trace ->
				trace.hasSpansSatisfyingExactlyInAnyOrder(
					s -> s.hasName("NestedTracingTest$TracingExampleTestCase")
						.hasNoParent()
						.hasAttribute(
							AttributeKey.stringKey("org.junit.test.unique_id"),
							uniqueId(NestedTracingTest.TracingExampleTestCase.class))
						.hasAttribute(
							AttributeKey.stringKey("org.junit.test.class"),
							NestedTracingTest.TracingExampleTestCase.class.getCanonicalName()
						)
						.hasAttribute(
							AttributeKey.stringKey("org.junit.test.lifecycle"),
							"PRE_INSTANCE_CONSTRUCT"
						),
					s -> s.hasName("succeedingParentTest()")
					// TODO missing spans for NestedTracingExampleTestCase & succeedingNestedTest
				)
		);
	}

	@TestCase
	@ExtendWith(OpenTelemetryTracing.class)
	static class TracingExampleTestCase {

		@Test
		void succeedingParentTest() {
			assertTrue(true);
		}

		@TestCase
		@Nested
		static class NestedTracingExampleTestCase {

			@Test
			void succeedingNestedTest() {
				assertTrue(true);
			}

		}
	}

}
