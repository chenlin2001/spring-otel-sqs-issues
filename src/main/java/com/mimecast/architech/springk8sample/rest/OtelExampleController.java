package com.mimecast.architech.springk8sample.rest;

import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.propagation.TextMapSetter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/otel")
@Log4j2
public class OtelExampleController {

    private final CloseableHttpClient httpClient;

    @Setter
    private int port;

    public OtelExampleController(CloseableHttpClient httpClient,
                                 @Value("${server.port:8080}") int port) {
        this.httpClient = httpClient;
        this.port = port;
    }

    @GetMapping("/example")
    public void example() throws Exception{
        String hopUri = String.format("http://localhost:%d/otel/hop", port);
        HttpGet getRequest = new HttpGet(hopUri);


        W3CTraceContextPropagator w3CTraceContextPropagator = W3CTraceContextPropagator.getInstance();
        //This code is needed as we don't have an OpenTelemetry Agent running. We can choose how we do this in our exported clients:
        // 1. Expect the teams to be running the client on a server where the OpenTelemetry Java Agent is configured
        // 2. Use the w3CTraceContextPropagator in the client we provide - it is the default propagator for OpenTelemetry and adds the traceparent and tracestate headers that are used by the OpenTelemetry Java Agent instrumentation when receiving API
        w3CTraceContextPropagator
                .inject(Context.current(), getRequest, new TextMapSetter<HttpGet>() {
                    @Override
                    public void set(HttpGet carrier, String key, String value) {
                        carrier.setHeader(key, value);
                    }
                });

        try (CloseableHttpResponse response = httpClient.execute(getRequest)) {
            // Check the response status
            int statusCode = response.getCode();

            // If the response is successful, print the response body
            if (statusCode != 200) {
                throw new RuntimeException("Failed to call hop: " + statusCode);
            }
        }

        log.info("after calling hop");
    }

    @GetMapping("/hop")
    public void hop() throws Exception {
        log.info("hop was called");
    }


}
