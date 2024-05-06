package com.example;

import com.nikolasgrottendieck.junit.otel.ObservedTests;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ObservedTests
public class SetUpAndTearDownTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(SetUpAndTearDownTest.class);

	@BeforeAll
	static void setUpAll() {
		LOGGER.info("set up all");
	}

	@BeforeEach
	void setUp() {
		LOGGER.info("set up each");
	}

	@AfterEach
	void tearDown() {
		LOGGER.info("tear down each");
	}

	@AfterAll
	static void tearDownAll() {
		LOGGER.info("tear down all");
	}

	@Test
	void succeedingTest() {
		assertTrue(true);
	}

}
