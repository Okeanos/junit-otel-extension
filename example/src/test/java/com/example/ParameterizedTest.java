package com.example;

import com.nikolasgrottendieck.junit.otel.ObservedTests;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

@ObservedTests
public class ParameterizedTest {

	@org.junit.jupiter.params.ParameterizedTest
	@ValueSource(ints = {1, 2, 3})
	void testParameter(int param) {
		assertTrue(param > 1);
		assumeTrue(param < 3);
	}

	@org.junit.jupiter.params.ParameterizedTest(
		name = "{index} ==> param ''{0}''"
	)
	@ValueSource(ints = {1, 2, 3})
	void testNamedParameter(int param) {
		assertTrue(param > 1);
		assumeTrue(param < 3);
	}

}
