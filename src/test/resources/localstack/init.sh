#!/bin/sh

awslocal sqs create-queue --queue-name test-sqs-queue-name

echo "Initialized."
