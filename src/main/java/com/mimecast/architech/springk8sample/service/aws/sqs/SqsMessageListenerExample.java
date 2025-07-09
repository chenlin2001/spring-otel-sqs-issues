package com.mimecast.architech.springk8sample.service.aws.sqs;

import com.mimecast.architech.springk8sample.model.SqsMessage;
import io.awspring.cloud.sqs.annotation.SqsListener;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.context.Context;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

@Service
@Log4j2
@ConditionalOnProperty(name = "sqs.enabled", havingValue = "true", matchIfMissing = false)
public class SqsMessageListenerExample {
    private final SqsMessageHandler sqsMessageHandler;

    public SqsMessageListenerExample(SqsMessageHandler sqsMessageHandler) {
        this.sqsMessageHandler = sqsMessageHandler;
    }

    @SqsListener(value = "${sqs.queue.name}")
    public void listen(SqsMessage sqsMessage, 
                      @Header(value = "traceparent", required = false) String traceParent, 
                      @Header(value = "myAttribute", required = false) String myAttribute) {
        log.info("Received message: {}", sqsMessage);
        log.info("Traceparent header: {}", traceParent);
        log.info("Custom attribute myAttribute: {}", myAttribute);
        
        // Handle nullable headers safely
        if (traceParent != null) {
            log.info("Traceparent is present: {}", traceParent);
        } else {
            log.info("Traceparent header is not present");
        }
        
        if (myAttribute != null) {
            log.info("myAttribute is present: {}", myAttribute);
        } else {
            log.info("myAttribute header is not present");
        }
        
        Context currentContext = Context.current();
        Span span = Span.fromContext(currentContext);
        if (span == null || !span.getSpanContext().isValid()) {
            log.error("No valid OpenTelemetry span found in the current context");
        }else{
            sqsMessageHandler.handleMessage(sqsMessage);
        }
    }
} 