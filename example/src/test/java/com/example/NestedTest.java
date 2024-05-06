package com.example;

import com.nikolasgrottendieck.junit.otel.ObservedTests;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ObservedTests
public class NestedTest {

	@Test
	void succeedingParentTest() {
		assertTrue(true);
	}

	@Nested
	class NestedTracingExampleTestCase {

		@Test
		void succeedingNestedTest() {
			assertTrue(true);
		}

	}
}
