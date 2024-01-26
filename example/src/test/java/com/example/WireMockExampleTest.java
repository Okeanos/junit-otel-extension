package com.example;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.nikolasgrottendieck.junit.otel.ObservedTests;
import org.apache.http.client.fluent.Request;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@WireMockTest
@ObservedTests
public class WireMockExampleTest {

	@Test
	void test_something_with_wiremock(WireMockRuntimeInfo wmRuntimeInfo) throws IOException {
		// The static DSL will be automatically configured for you
		WireMock.stubFor(WireMock.get("/static-dsl").willReturn(WireMock.ok()));

		// Instance DSL can be obtained from the runtime info parameter
		WireMock wireMock = wmRuntimeInfo.getWireMock();
		wireMock.register(WireMock.get("/instance-dsl").willReturn(WireMock.ok()));

		// Info such as port numbers is also available
		int port = wmRuntimeInfo.getHttpPort();

		var response = Request.Get("http://localhost:" + port + "/static-dsl").execute().returnResponse();
		assertEquals(response.getStatusLine().getStatusCode(), 200);
	}
}
