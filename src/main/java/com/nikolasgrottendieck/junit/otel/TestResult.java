package com.nikolasgrottendieck.junit.otel;

/**
 * The JUnit test result names, i.e. which states can tests assume after running.
 */
public enum TestResult {
	ABORTED,
	FAILED,
	SKIPPED,
	SUCCESSFUL,
}
