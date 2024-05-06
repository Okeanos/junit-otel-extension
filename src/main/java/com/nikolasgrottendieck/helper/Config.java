package com.nikolasgrottendieck.helper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Loads the config properties file containing e.g. the project's version number. Makes versioning the OTEL provider easier and consistent.
 */
public final class Config {

	private static final String CONFIG_FILE = "config.properties";

	private static final Properties PROPERTIES = new Properties();
	private static boolean failedLoading = false;

	private Config() {
	}

	private static void loadProperties() {
		try (InputStream in = Config.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
			PROPERTIES.load(in);
		} catch (IOException e) {
			failedLoading = true;
		}
	}

	public static String getVersion() {
		if (PROPERTIES.isEmpty() && !failedLoading) {
			loadProperties();
		}

		return PROPERTIES.getProperty("instrumentationVersion", "0.0.1-SNAPSHOT");
	}

	public static String getMetricsNamespace() {
		if (PROPERTIES.isEmpty() && !failedLoading) {
			loadProperties();
		}

		return PROPERTIES.getProperty("metricsNamespace", "opentelemetry-metrics");
	}

	public static String getTracingNamespace() {
		if (PROPERTIES.isEmpty() && !failedLoading) {
			loadProperties();
		}

		return PROPERTIES.getProperty("tracingNamespace", "opentelemetry-tracing");
	}

}
