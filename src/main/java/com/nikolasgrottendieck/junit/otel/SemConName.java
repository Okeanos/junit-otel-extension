package com.nikolasgrottendieck.junit.otel;

/**
 * Semantic Convention Names based on <a href="https://opentelemetry.io/docs/specs/semconv/">OpenTelemetry Semantic Conventions</a>
 */
public enum SemConName {
	ARGUMENTS("org.junit.test.arguments", "Test arguments"),
	CLASS("org.junit.test.class", "Test class name"),
	INSTANCE_LIFECYCLE("org.junit.test.instance.lifecycle", "JUnit test instance lifecycle state"),
	LIFECYCLE("org.junit.test.lifecycle", "JUnit test lifecycle state"),
	METHOD("org.junit.test.method", "Test method name"),
	RESULT("org.junit.test.result", "Test result"),
	RESULT_REASON("org.junit.test.result.reason", "Reason for the test result"),
	TAG("org.junit.test.tag", "JUnit test tag"),
	TEST_ABORT_COUNTER("org.junit.test.aborted", "Counts the number of aborted tests"),
	TEST_COUNTER("org.junit.test.counter", "Counts the number of tests"),
	TEST_DISABLED_COUNTER("org.junit.test.disabled", "Counts the number of disabled tests"),
	TEST_FAILURE_COUNTER("org.junit.test.failed", "Counts the number of failed tests"),
	TEST_SUCCESS_COUNTER("org.junit.test.successful", "Counts the number of successful tests"),
	UNIQUE_ID("org.junit.test.unique_id", "Unique test id"),
	;

	private final String otelName;
	private final String description;

	SemConName(final String otelName, final String description) {
		this.otelName = otelName;
		this.description = description;
	}

	public String getOtelName() {
		return otelName;
	}

	public String getDescription() {
		return description;
	}
}
