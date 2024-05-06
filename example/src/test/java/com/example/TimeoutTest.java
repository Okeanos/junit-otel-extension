package com.example;

import com.nikolasgrottendieck.junit.otel.ObservedTests;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.concurrent.TimeUnit;

import static java.lang.Thread.sleep;

@ObservedTests
public class TimeoutTest {

	@Test
	@Timeout(value = 500, unit = TimeUnit.MILLISECONDS)
	void timeoutTest() throws InterruptedException {
		sleep(600);
	}

	@Test
	@Timeout(value = 500, unit = TimeUnit.MILLISECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
	void timeoutInSeparateThread() throws InterruptedException {
		sleep(600);
	}

}
