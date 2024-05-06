package com.nikolasgrottendieck.junit.otel;

import org.jspecify.annotations.Nullable;

enum TestLifecycle {
	PRE_INSTANCE_CONSTRUCT(null, "testInstanceSpan", "testInstanceScope"),
	TEST_EXECUTION(PRE_INSTANCE_CONSTRUCT, "testExecutionSpan", "testExecutionScope"),
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

