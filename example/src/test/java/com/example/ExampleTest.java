package com.example;

import com.nikolasgrottendieck.junit.otel.ObservedTests;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ObservedTests
public class ExampleTest {

	@BeforeEach
	void setUp() {
	}

	@AfterEach
	void tearDown() {
	}

	@Test
	void testSucceeds() {
		assertEquals(2, 2);
	}

	@Test
	void testFails() {
		assertEquals(2, 1);
	}

	@ParameterizedTest
	@ValueSource(strings = {"racecar", "radar", "able was I ere I saw elba"})
	void parameterizedTest(String value) {
		assertTrue(value.length() < 6);
	}

	@Test
	@DisplayName("😱")
	void namedTest() {
		assertTrue(true);
	}

	@Disabled("Disabled for now")
	@Test
	void testWillBeSkipped() {
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
