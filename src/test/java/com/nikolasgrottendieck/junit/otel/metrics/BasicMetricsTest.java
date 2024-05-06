package com.nikolasgrottendieck.junit.otel.metrics;

import com.nikolasgrottendieck.helper.TestCase;
import com.nikolasgrottendieck.junit.otel.OpenTelemetryMetrics;
import io.opentelemetry.sdk.testing.assertj.OpenTelemetryAssertions;
import io.opentelemetry.sdk.testing.junit5.OpenTelemetryExtension;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;

import static com.nikolasgrottendieck.helper.TestCaseHelpers.engineTestKit;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

class BasicMetricsTest {

	@RegisterExtension
	private static final OpenTelemetryExtension otelTesting = OpenTelemetryExtension.create();

	@Test
	void verifyMetrics() {
		engineTestKit()
			.selectors(selectClass(MetricsExampleTest.class))
			.execute()
			.testEvents()
			.assertStatistics(stats ->
				stats.skipped(1)
					.started(3)
					.succeeded(1)
					.aborted(1)
					.failed(1)
			);

		assertThat(otelTesting.getMetrics())
			.satisfiesExactlyInAnyOrder(
				metricData ->
					OpenTelemetryAssertions.assertThat(metricData)
						.hasName("org.junit.test.aborted")
						.hasLongSumSatisfying(
							sum -> sum.hasPointsSatisfying(point -> point.hasValue(1)
							)
						),
				metricData ->
					OpenTelemetryAssertions.assertThat(metricData)
						.hasName("org.junit.test.counter")
						.hasLongSumSatisfying(
							sum -> sum.hasPointsSatisfying(point -> point.hasValue(4)
							)
						),
				metricData ->
					OpenTelemetryAssertions.assertThat(metricData)
						.hasName("org.junit.test.disabled")
						.hasLongSumSatisfying(
							sum -> sum.hasPointsSatisfying(point -> point.hasValue(1)
							)
						),
				metricData ->
					OpenTelemetryAssertions.assertThat(metricData)
						.hasName("org.junit.test.failed")
						.hasLongSumSatisfying(
							sum -> sum.hasPointsSatisfying(point -> point.hasValue(1)
							)
						),
				metricData ->
					OpenTelemetryAssertions.assertThat(metricData)
						.hasName("org.junit.test.successful")
						.hasLongSumSatisfying(
							sum -> sum.hasPointsSatisfying(point -> point.hasValue(1)
							)
						)
			);
	}

	@TestCase
	@ExtendWith(OpenTelemetryMetrics.class)
	@TestMethodOrder(OrderAnnotation.class)
	static class MetricsExampleTest {

		@Test
		@Disabled("for demonstration purposes")
		@Order(1)
		void skippedTest() {
			// skipped ...
		}

		@Test
		@Order(2)
		void succeedingTest() {
			assertTrue(true);
		}

		@Test
		@Order(3)
		void abortedTest() {
			assumeTrue("abc".contains("Z"), "abc does not contain Z");
			// aborted ...
		}

		@Test
		@Order(4)
		void failingTest() {
			fail("failed on purpose");
		}
	}

}
