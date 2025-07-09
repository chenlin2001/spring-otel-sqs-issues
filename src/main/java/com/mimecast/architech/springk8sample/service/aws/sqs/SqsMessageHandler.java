package com.mimecast.architech.springk8sample.service.aws.sqs;

import com.mimecast.architech.springk8sample.model.SqsMessage;

public interface SqsMessageHandler {
    void handleMessage(SqsMessage message);
} 