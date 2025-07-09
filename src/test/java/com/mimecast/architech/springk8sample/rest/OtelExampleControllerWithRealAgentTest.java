package com.mimecast.architech.springk8sample.rest;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.context.Scope;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Integration test demonstrating OpenTelemetry span capture with the Java agent.
 * This test shows how to test spans created within controller methods.
 * 
 * Note: Spans are captured by the OpenTelemetry agent and exported based on
 * the agent's configuration. For verification, use a test collector.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Log4j2
class OtelExampleControllerWithRealAgentTest {

    @LocalServerPort
    public int serverPort;

    @Autowired
    private OpenTelemetry openTelemetry;

    @Autowired 
    OtelExampleController otelExampleController;

    @PostConstruct
    public void initRestAssured() {
        RestAssured.port = serverPort;
        RestAssured.urlEncodingEnabled = false;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        otelExampleController.setPort(serverPort);
    }

    @Test
    public void callExample() {
        log.info("call example without a span");

        Response response = RestAssured.given()
                .accept(ContentType.JSON)
                .when()
                .get("/otel/example");
        assertEquals(200, response.statusCode());

        // Note: Spans are captured by the OpenTelemetry agent and exported
        // based on the agent's configuration. For verification, use a test collector.
    }

    @Test
    public void callExampleWithExistingSpan() {
        Span span = openTelemetry.getTracer("test")
                .spanBuilder("test-span")
                .setSpanKind(SpanKind.CLIENT)
                .startSpan();

        try (Scope scope = span.makeCurrent()) {
            log.info("call example with existing span");

            Response response = RestAssured.given()
                    .accept(ContentType.JSON)
                    .when()
                    .get("/otel/example");
            assertEquals(200, response.statusCode());

        } finally {
            span.end();
        }

        // Note: Spans are captured by the OpenTelemetry agent and exported
        // based on the agent's configuration. For verification, use a test collector.
    }
}