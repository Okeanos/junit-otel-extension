package com.nikolasgrottendieck.junit.otel.helper;

import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;

public class TestCaseCondition implements ExecutionCondition {

	public static final String EXECUTE_TEST_CASE = "EXECUTE_TEST_CASE";

	@Override
	public ConditionEvaluationResult evaluateExecutionCondition(final ExtensionContext context) {
		return context
			.getConfigurationParameter(EXECUTE_TEST_CASE).isPresent()
			? ConditionEvaluationResult.enabled("Test enabled because EngineTestKit is correctly configured.")
			: ConditionEvaluationResult.disabled("Tests annotated with @TestCase are run only when executed with a properly configured EngineTestKit (EXECUTE_TEST_CASE configuration parameter is set).");
	}
}
