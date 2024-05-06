package com.nikolasgrottendieck.junit.otel;

import org.jspecify.annotations.Nullable;

/**
 * The JUnit test lifecycle names. See also <a href="https://junit.org/junit5/docs/current/user-guide/#extensions-lifecycle-callbacks>Extension Lifecycle Callbacks</a> in the official JUnit documentation.
 */
enum TestLifecycle {
	PRE_INSTANCE_CONSTRUCT(null, "testInstanceSpan", "testInstanceScope"),
	TEST_FACTORY(PRE_INSTANCE_CONSTRUCT, "testFactorySpan", "testFactoryScope"),
	TEST_CLASS(TEST_FACTORY, "testClassSpan", "testClassScope"),
	TEST_TEMPLATE(PRE_INSTANCE_CONSTRUCT, "testTemplateSpan", "testTemplateScope"),
	DYNAMIC_TEST(PRE_INSTANCE_CONSTRUCT, "dynamicTestSpan", "dynamicTestScope"),
	BEFORE_ALL(TEST_CLASS, "beforeAllSpan", "beforeAllScope"),
	BEFORE_EACH(TEST_CLASS, "beforeEachSpan", "beforeEachScope"),
	BEFORE_EXECUTION(TEST_CLASS, "beforeExecutionSpan", "beforeExecutionScope"),
	TEST_EXECUTION(TEST_CLASS, "testExecutionSpan", "testExecutionScope"),
	AFTER_EXECUTION(TEST_CLASS, "afterExecutionSpan", "afterExecutionScope"),
	AFTER_EACH(TEST_CLASS, "testExecutionSpan", "testExecutionScope"),
	AFTER_ALL(TEST_CLASS, "testExecutionSpan", "testExecutionScope"),
	;

	private final TestLifecycle parent;
	private final String spanName;
	private final String scopeName;

	TestLifecycle(@Nullable final TestLifecycle parent, final String spanName, final String scopeName) {
		this.parent = parent;
		this.spanName = spanName;
		this.scopeName = scopeName;
	}

	public TestLifecycle getParent() {
		return parent;
	}

	public String getSpanName() {
		return spanName;
	}

	public String getScopeName() {
		return scopeName;
	}
}

