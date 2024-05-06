package com.nikolasgrottendieck.junit.otel.tracing;

import com.nikolasgrottendieck.helper.TestCase;
import com.nikolasgrottendieck.junit.otel.OpenTelemetryTracing;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.sdk.testing.junit5.OpenTelemetryExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static com.nikolasgrottendieck.helper.TestCaseHelpers.engineTestKit;
import static com.nikolasgrottendieck.helper.TestCaseHelpers.uniqueId;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

class ParameterizedTestTracingTest {

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
					.dynamicallyRegistered(3)
					.started(3)
			);

		// TODO assertions here are incomplete and don't cover all expected spans & traces
		otelTesting.assertTraces().hasTracesSatisfyingExactly(
			trace ->
				trace.hasSpansSatisfyingExactlyInAnyOrder(
					s -> s.hasName("ParameterizedTestTracingTest$TracingExampleTestCase")
						.hasNoParent()
						.hasAttribute(
							AttributeKey.stringKey("org.junit.test.unique_id"),
							uniqueId(ParameterizedTestTracingTest.TracingExampleTestCase.class))
						.hasAttribute(
							AttributeKey.stringKey("org.junit.test.class"),
							ParameterizedTestTracingTest.TracingExampleTestCase.class.getCanonicalName()
						)
						.hasAttribute(
							AttributeKey.stringKey("org.junit.test.lifecycle"),
							"PRE_INSTANCE_CONSTRUCT"
						)
				),
			trace ->
				trace.hasSpansSatisfyingExactlyInAnyOrder(
					s -> s.hasName("ParameterizedTestTracingTest$TracingExampleTestCase")
						.hasNoParent()
						.hasAttribute(
							AttributeKey.stringKey("org.junit.test.unique_id"),
							uniqueId(ParameterizedTestTracingTest.TracingExampleTestCase.class))
						.hasAttribute(
							AttributeKey.stringKey("org.junit.test.class"),
							ParameterizedTestTracingTest.TracingExampleTestCase.class.getCanonicalName()
						)
						.hasAttribute(
							AttributeKey.stringKey("org.junit.test.lifecycle"),
							"PRE_INSTANCE_CONSTRUCT"
						)
				),
			trace ->
				trace.hasSpansSatisfyingExactlyInAnyOrder(
					s -> s.hasName("ParameterizedTestTracingTest$TracingExampleTestCase")
						.hasNoParent()
						.hasAttribute(
							AttributeKey.stringKey("org.junit.test.unique_id"),
							uniqueId(ParameterizedTestTracingTest.TracingExampleTestCase.class))
						.hasAttribute(
							AttributeKey.stringKey("org.junit.test.class"),
							ParameterizedTestTracingTest.TracingExampleTestCase.class.getCanonicalName()
						)
						.hasAttribute(
							AttributeKey.stringKey("org.junit.test.lifecycle"),
							"PRE_INSTANCE_CONSTRUCT"
						)
				)
		);
	}

	@Test
	void verifyNamedSpans() {
		engineTestKit()
			.selectors(selectClass(TracingExampleNamedTestCase.class))
			.execute()
			.testEvents()
			.assertStatistics(stats ->
				stats
					.aborted(1)
					.dynamicallyRegistered(3)
					.started(3)
			);

		// TODO assertions here are incomplete and don't cover all expected spans & traces
		otelTesting.assertTraces().hasTracesSatisfyingExactly(
			trace ->
				trace.hasSpansSatisfyingExactly(
					s -> s.hasName("ParameterizedTestTracingTest$TracingExampleNamedTestCase")
						.hasNoParent()
						.hasAttribute(
							AttributeKey.stringKey("org.junit.test.unique_id"),
							uniqueId(ParameterizedTestTracingTest.TracingExampleNamedTestCase.class))
						.hasAttribute(
							AttributeKey.stringKey("org.junit.test.class"),
							ParameterizedTestTracingTest.TracingExampleNamedTestCase.class.getCanonicalName()
						)
						.hasAttribute(
							AttributeKey.stringKey("org.junit.test.lifecycle"),
							"PRE_INSTANCE_CONSTRUCT"
						)
				),
			trace ->
				trace.hasSpansSatisfyingExactly(
					s -> s.hasName("ParameterizedTestTracingTest$TracingExampleNamedTestCase")
						.hasNoParent()
						.hasAttribute(
							AttributeKey.stringKey("org.junit.test.unique_id"),
							uniqueId(ParameterizedTestTracingTest.TracingExampleNamedTestCase.class))
						.hasAttribute(
							AttributeKey.stringKey("org.junit.test.class"),
							ParameterizedTestTracingTest.TracingExampleNamedTestCase.class.getCanonicalName()
						)
						.hasAttribute(
							AttributeKey.stringKey("org.junit.test.lifecycle"),
							"PRE_INSTANCE_CONSTRUCT"
						)
				),
			trace ->
				trace.hasSpansSatisfyingExactly(
					s -> s.hasName("ParameterizedTestTracingTest$TracingExampleNamedTestCase")
						.hasNoParent()
						.hasAttribute(
							AttributeKey.stringKey("org.junit.test.unique_id"),
							uniqueId(ParameterizedTestTracingTest.TracingExampleNamedTestCase.class))
						.hasAttribute(
							AttributeKey.stringKey("org.junit.test.class"),
							ParameterizedTestTracingTest.TracingExampleNamedTestCase.class.getCanonicalName()
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

		@ParameterizedTest
		@ValueSource(ints = {1, 2, 3})
		void testParameter(int param) {
			assertTrue(param > 1);
			assumeTrue(param < 3);
		}

	}

	@TestCase
	@ExtendWith(OpenTelemetryTracing.class)
	static class TracingExampleNamedTestCase {

		@ParameterizedTest(
			name = "{index} ==> param ''{0}''"
		)
		@ValueSource(ints = {1, 2, 3})
		void testNamedParameter(int param) {
			assertTrue(param > 1);
			assumeTrue(param < 3);
		}
	}

}
