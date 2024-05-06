package com.example;

import com.nikolasgrottendieck.junit.otel.ObservedTests;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.stream.Stream;

@ObservedTests
public class DynamicTestsTest {

	@TestFactory
	Stream<DynamicContainer> dynamicTestsFromIntStream() {
		return Stream.of(DynamicContainer.dynamicContainer("dynamic-container",
			Stream.of(DynamicTest.dynamicTest("dynamicTest", Assertions::fail))));
	}

}
