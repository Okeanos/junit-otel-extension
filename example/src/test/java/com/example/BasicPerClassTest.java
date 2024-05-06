package com.example;

import com.nikolasgrottendieck.junit.otel.ObservedTests;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

@ObservedTests
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BasicPerClassTest {

	@Test
	void abortedTest() {
		assumeTrue("abc".contains("Z"), "abc does not contain Z");
		// aborted ...
	}

	@Test
	void failingTest() {
		fail("failed on purpose");
	}

	@Disabled("Disabled for now")
	@Test
	void testWillBeSkipped() {
	}

	@Test
	void succeedingTest() {
		assertTrue(true);
	}

	@Test
	void groupedAssertions() {
		// In a grouped assertion all assertions are executed, and all
		// failures will be reported together.
		assertAll("person",
			() -> assertEquals("Jane", "Jane"),
			() -> assertEquals("Doe", "Down")
		);
	}

}
