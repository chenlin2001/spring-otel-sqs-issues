package com.mimecast.architech.springk8sample.service.aws.sqs;

import com.mimecast.architech.springk8sample.model.SqsMessage;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class SqsMessageHandlerImpl implements SqsMessageHandler {

    @Override
    public void handleMessage(SqsMessage message) {
        log.info("Processing SQS message: {}", message);
        
        log.info("Successfully processed message with ID: {} and content: {}", 
                message.getId(), message.getMessage());
    }
} 