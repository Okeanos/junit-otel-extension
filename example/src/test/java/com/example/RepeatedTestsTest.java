package com.example;

import com.nikolasgrottendieck.junit.otel.ObservedTests;
import org.junit.jupiter.api.RepeatedTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ObservedTests
public class RepeatedTestsTest {

	@RepeatedTest(2)
	void succeedingTest() {
		assertTrue(true);
	}

}
