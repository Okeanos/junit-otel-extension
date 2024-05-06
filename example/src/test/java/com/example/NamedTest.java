package com.example;

import com.nikolasgrottendieck.junit.otel.ObservedTests;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

@ObservedTests
@DisplayName("Fancy Tracing Example Test Class")
public class NamedTest {

	@Test
	@DisplayName("An aborted test")
	void abortedTest() {
		assumeTrue("abc".contains("Z"), "abc does not contain Z");
		// aborted ...
	}

	@Test
	@DisplayName("A failing test")
	void failingTest() {
		fail("failed on purpose");
	}

	@Test
	@Disabled("for demonstration purposes")
	@DisplayName("A skipped/disabled test")
	void skippedTest() {
		// skipped ...
	}

	@Test
	@DisplayName("A succeeding Test")
	void succeedingTest() {
		assertTrue(true);
	}

	@Test
	@DisplayName("✅ Has an Emoji Name")
	void emojiTest() {
		assertTrue(true);
	}

}
