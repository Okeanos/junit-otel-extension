#!/usr/bin/env bash

maven=mvn
if ! command -v mvn &>/dev/null; then
	maven=./mvnw
fi

mkdir -p "target"
if [[ ! -f "target/otelagent.jar" ]]; then
	agent_version=$($maven help:evaluate -Dexpression=opentelemetry-agent.version -q -DforceStdout)

	$maven dependency:get \
		-DgroupId=io.opentelemetry.javaagent \
		-DartifactId=opentelemetry-javaagent \
		-Dversion="${agent_version}" \
		-Dpackaging=jar

	cp "${HOME}/.m2/repository/io/opentelemetry/javaagent/opentelemetry-javaagent/${agent_version}/opentelemetry-javaagent-${agent_version}.jar" \
		"target/otelagent.jar"
fi

OTEL_EXPORTER_OTLP_ENDPOINT="http://localhost:4318" \
	OTEL_TRACES_EXPORTER="otlp" \
	OTEL_EXPORTER_OTLP_PROTOCOL="http/protobuf" \
	OTEL_EXPORTER_OTLP_ENDPOINT="http://localhost:4317" \
	OTEL_EXPORTER_OTLP_PROTOCOL="grpc" \
	MAVEN_OPTS="-javaagent:target/otelagent.jar -Dotel.javaagent.configuration-file=.mvn/otel.config" \
	$maven test -P otel
