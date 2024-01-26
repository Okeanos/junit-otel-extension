package com.nikolasgrottendieck.junit.otel.simple_tracing;

import com.nikolasgrottendieck.junit.otel.OpenTelemetrySimpleTracing;
import com.nikolasgrottendieck.junit.otel.helper.TestCase;
import io.opentelemetry.sdk.testing.junit5.OpenTelemetryExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.concurrent.TimeUnit;

import static com.nikolasgrottendieck.junit.otel.helper.TestCaseHelpers.engineTestKit;
import static java.lang.Thread.sleep;
import static org.assertj.core.api.Assertions.assertThat;
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

		assertThat(otelTesting.getSpans())
			.hasSize(3)
//			.satisfiesExactly(spanData -> {
//					assertThat(spanData.getName()).isEqualTo("TimeoutTracingTest$TracingExampleTestCase");
//					assertThat(spanData.getParentSpanContext().getTraceId()).isEqualTo("00000000000000000000000000000000");
//					assertThat(spanData.getParentSpanId()).isEqualTo("0000000000000000");
//
//					assertThat(spanData.getAttributes().asMap())
//						.contains(
//							entry(AttributeKey.stringKey(CLASS.getOtelName()), TracingExampleTestCase.class.getCanonicalName()),
//							entry(AttributeKey.stringKey(LIFECYCLE.getOtelName()), PRE_INSTANCE_CONSTRUCT.toString()),
//							entry(AttributeKey.stringKey(UNIQUE_ID.getOtelName()), uniqueId(TracingExampleTestCase.class))
//						);
//					assertThat(spanData.hasEnded()).isTrue();
//				}
//			)
		;
	}

	@TestCase
	@ExtendWith(OpenTelemetrySimpleTracing.class)
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
