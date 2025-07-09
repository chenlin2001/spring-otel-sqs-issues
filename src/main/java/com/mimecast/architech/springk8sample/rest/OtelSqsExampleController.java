package com.mimecast.architech.springk8sample.rest;

import com.mimecast.architech.springk8sample.service.otel.OtelSqsExampleService;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/otel-sqs")
@Log4j2
@ConditionalOnProperty(name = "sqs.enabled", havingValue = "true", matchIfMissing = false)
public class OtelSqsExampleController {

    private final OtelSqsExampleService otelSqsExampleService;

    public OtelSqsExampleController(OtelSqsExampleService otelSqsExampleService) {
        this.otelSqsExampleService = otelSqsExampleService;
    }

    @GetMapping("/send")
    public void sendMessage() {
        otelSqsExampleService.sendMessage();
    }

    @GetMapping("/example")
    public void example() {
        otelSqsExampleService.sendMessage();
    }
} 