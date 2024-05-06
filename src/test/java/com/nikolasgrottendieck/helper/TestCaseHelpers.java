package com.nikolasgrottendieck.helper;

import org.junit.jupiter.engine.descriptor.JupiterEngineDescriptor;
import org.junit.platform.testkit.engine.EngineTestKit;

import static com.nikolasgrottendieck.helper.TestCaseCondition.EXECUTE_TEST_CASE;

public final class TestCaseHelpers {

	private TestCaseHelpers() {
	}

	public static EngineTestKit.Builder engineTestKit() {
		return EngineTestKit
			.engine(JupiterEngineDescriptor.ENGINE_ID)
			.configurationParameter(EXECUTE_TEST_CASE, "true");
	}

	public static String uniqueId(final Class<?> clazz) {
		return String.format("[engine:%s]/[class:%s]", JupiterEngineDescriptor.ENGINE_ID, clazz.getName());
	}

	public static String uniqueId(final Class<?> clazz, final String methodName) {
		return String.format("[engine:%s]/[class:%s]/[method:%s]", JupiterEngineDescriptor.ENGINE_ID, clazz.getName(), methodName);
	}
}
