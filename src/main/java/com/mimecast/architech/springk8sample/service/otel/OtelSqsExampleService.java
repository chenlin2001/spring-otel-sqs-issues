package com.mimecast.architech.springk8sample.service.otel;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mimecast.architech.springk8sample.model.SqsMessage;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Log4j2
@ConditionalOnProperty(name = "sqs.enabled", havingValue = "true", matchIfMissing = false)
public class OtelSqsExampleService {

    private final SqsTemplate sqsTemplate;
    private final ObjectMapper objectMapper;
    private final String queueName;

    public OtelSqsExampleService(SqsTemplate sqsTemplate, ObjectMapper objectMapper,
                                 @Value("${sqs.queue.name}") String queueName) {
        this.sqsTemplate = sqsTemplate;
        this.objectMapper = objectMapper;
        this.queueName = queueName;
    }

    public void sendMessage() {
        try {
            SqsMessage sqsMessage = SqsMessage.builder().id(1l).message("message").build();

            String messageBody = objectMapper.writeValueAsString(sqsMessage);
            
            // Build message with attributes using MessageBuilder
            var message = MessageBuilder
                .withPayload(messageBody)
                .setHeader("myAttribute", "Chen")
                .build();
            
            // Send message with attributes
            sqsTemplate.send(queueName, message);
            log.info("Message sent to SQS queue: {}", message);
        } catch (Exception e) {
            log.error("Error sending message to SQS", e);
            throw new RuntimeException("Failed to send message to SQS", e);
        }
    }
}