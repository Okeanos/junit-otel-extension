package com.nikolasgrottendieck.junit.otel.tracing;

import com.nikolasgrottendieck.helper.TestCase;
import com.nikolasgrottendieck.junit.otel.OpenTelemetryTracing;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.sdk.testing.junit5.OpenTelemetryExtension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.stream.Stream;

import static com.nikolasgrottendieck.helper.TestCaseHelpers.engineTestKit;
import static com.nikolasgrottendieck.helper.TestCaseHelpers.uniqueId;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

class DynamicTestsTracingTest {

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
					.started(1)
					.dynamicallyRegistered(1)
					.failed(1)
			);

		// TODO assertions here are incomplete and don't cover all expected spans & traces
		otelTesting.assertTraces().hasTracesSatisfyingExactly(
			trace ->
				trace.hasSpansSatisfyingExactlyInAnyOrder(
					s -> s.hasName("DynamicTestsTracingTest$TracingExampleTestCase")
						.hasNoParent()
						.hasAttribute(
							AttributeKey.stringKey("org.junit.test.unique_id"),
							uniqueId(DynamicTestsTracingTest.TracingExampleTestCase.class))
						.hasAttribute(
							AttributeKey.stringKey("org.junit.test.class"),
							DynamicTestsTracingTest.TracingExampleTestCase.class.getCanonicalName()
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

		@TestFactory
		Stream<DynamicContainer> dynamicTestsFromIntStream() {
			return Stream.of(DynamicContainer.dynamicContainer("dynamic-container",
				Stream.of(DynamicTest.dynamicTest("dynamicTest", Assertions::fail))));
		}
	}

}
