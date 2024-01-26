#!/usr/bin/env bash

maven=mvn
if ! command -v mvn &> /dev/null
then
	maven=./mvnw
fi

mkdir -p "target"
if [[ ! -f "target/otelagent.jar" ]]; then
	$maven dependency:get -DgroupId=io.opentelemetry.javaagent -DartifactId=opentelemetry-javaagent -Dversion=2.0.0 -Dpackaging=jar

	cp "${HOME}/.m2/repository/io/opentelemetry/javaagent/opentelemetry-javaagent/2.0.0/opentelemetry-javaagent-2.0.0.jar" \
		"target/otelagent.jar"
fi

OTEL_TRACES_EXPORTER="otlp" $maven clean verify
