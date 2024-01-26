package com.nikolasgrottendieck.junit.otel.simple_tracing;

import com.nikolasgrottendieck.junit.otel.OpenTelemetrySimpleTracing;
import com.nikolasgrottendieck.junit.otel.helper.TestCase;
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

import static com.nikolasgrottendieck.junit.otel.SemConName.CLASS;
import static com.nikolasgrottendieck.junit.otel.SemConName.LIFECYCLE;
import static com.nikolasgrottendieck.junit.otel.SemConName.UNIQUE_ID;
import static com.nikolasgrottendieck.junit.otel.TestLifecycle.PRE_INSTANCE_CONSTRUCT;
import static com.nikolasgrottendieck.junit.otel.helper.TestCaseHelpers.engineTestKit;
import static com.nikolasgrottendieck.junit.otel.helper.TestCaseHelpers.uniqueId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
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

		assertThat(otelTesting.getSpans())
			.hasSize(1)
			.satisfiesExactly(spanData -> {
				/* Example Root Span
				SpanData{
					spanContext=ImmutableSpanContext{
						traceId=366dcd040b56bc5ed0c61e97df5f3614,
						spanId=2630513556a0cab8,
						traceFlags=01,
						traceState=ArrayBasedTraceState{
							entries=[]},
							remote=false,
							valid=true
						},
						parentSpanContext=ImmutableSpanContext{
							traceId=00000000000000000000000000000000,
							spanId=0000000000000000,
							traceFlags=00,
							traceState=ArrayBasedTraceState{
								entries=[]
							},
							remote=false,
							valid=false
						},
						resource=Resource{
							schemaUrl=null,
							attributes={
								service.name="unknown_service:java",
								telemetry.sdk.language="java",
								telemetry.sdk.name="opentelemetry",
								telemetry.sdk.version="1.34.1"
							}
						},
						instrumentationScopeInfo=InstrumentationScopeInfo{
							name=com.nikolasgrottendieck.junit.otel,
							version=0.0.1,
							schemaUrl=null,
							attributes={}
						},
						name=BasicTracingTest$TracingExampleTestCase,
						kind=INTERNAL,
						startEpochNanos=1706735220223369000,
						endEpochNanos=1706735220229920897,
						attributes=AttributesMap{
							data={
								org.junit.test.class=com.nikolasgrottendieck.junit.otel.simple_tracing.BasicTracingTest.TracingExampleTestCase,
								org.junit.test.lifecycle=PRE_INSTANCE_CONSTRUCT,
								org.junit.test.unique_id=[engine:junit-jupiter]/[class:com.nikolasgrottendieck.junit.otel.simple_tracing.BasicTracingTest$TracingExampleTestCase]
							},
							capacity=128,
							totalAddedValues=3
						},
						totalAttributeCount=3,
						events=[],
						totalRecordedEvents=0,
						links=[],
						totalRecordedLinks=0,
						status=ImmutableStatusData{
							statusCode=UNSET,
							description=
						},
						hasEnded=true
					}
				 */
				assertThat(spanData.getName()).isEqualTo("DynamicTestsTracingTest$TracingExampleTestCase");
				assertThat(spanData.getParentSpanContext().getTraceId()).isEqualTo("00000000000000000000000000000000");
				assertThat(spanData.getParentSpanId()).isEqualTo("0000000000000000");

				assertThat(spanData.getAttributes().asMap())
					.contains(
						entry(AttributeKey.stringKey(CLASS.getOtelName()), TracingExampleTestCase.class.getCanonicalName()),
						entry(AttributeKey.stringKey(LIFECYCLE.getOtelName()), PRE_INSTANCE_CONSTRUCT.toString()),
						entry(AttributeKey.stringKey(UNIQUE_ID.getOtelName()), uniqueId(TracingExampleTestCase.class))
					);
				assertThat(spanData.hasEnded()).isTrue();
			});
	}

	@TestCase
	@ExtendWith(OpenTelemetrySimpleTracing.class)
	static class TracingExampleTestCase {

		@TestFactory
		Stream<DynamicContainer> dynamicTestsFromIntStream() {
			return Stream.of(DynamicContainer.dynamicContainer("dynamic-container",
				Stream.of(DynamicTest.dynamicTest("dynamicTest", Assertions::fail))));
		}
	}

}
