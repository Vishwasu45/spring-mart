package com.springmart.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;

/**
 * Service for managing SQS operations.
 * Handles sending and receiving messages from SQS queues for asynchronous
 * processing.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SQSService {

    private final SqsClient sqsClient;

    @Value("${app.aws.sqs.order-queue-name}")
    private String orderQueueName;

    @Value("${app.aws.sqs.order-dlq-name}")
    private String orderDlqName;

    private String orderQueueUrl;
    private String orderDlqUrl;

    /**
     * Sends an order message to the SQS queue.
     *
     * @param orderId the ID of the order to process
     * @return message ID
     */
    public String sendOrderMessage(Long orderId) {
        try {
            ensureQueueExists();

            SendMessageRequest sendMessageRequest = SendMessageRequest.builder()
                    .queueUrl(getOrderQueueUrl())
                    .messageBody(orderId.toString())
                    .messageAttributes(java.util.Map.of(
                            "OrderId", MessageAttributeValue.builder()
                                    .stringValue(orderId.toString())
                                    .dataType("String")
                                    .build(),
                            "Timestamp", MessageAttributeValue.builder()
                                    .stringValue(String.valueOf(System.currentTimeMillis()))
                                    .dataType("Number")
                                    .build()))
                    .build();

            SendMessageResponse response = sqsClient.sendMessage(sendMessageRequest);
            log.info("Sent order #{} to SQS queue. Message ID: {}", orderId, response.messageId());

            return response.messageId();

        } catch (Exception e) {
            log.error("Failed to send order #{} to SQS", orderId, e);
            throw new RuntimeException("Failed to send order to processing queue", e);
        }
    }

    /**
     * Gets the order queue URL, fetching it if not already cached.
     *
     * @return queue URL
     */
    private String getOrderQueueUrl() {
        if (orderQueueUrl == null) {
            orderQueueUrl = getQueueUrl(orderQueueName);
        }
        return orderQueueUrl;
    }

    /**
     * Gets the order DLQ URL, fetching it if not already cached.
     *
     * @return DLQ URL
     */
    private String getOrderDlqUrl() {
        if (orderDlqUrl == null) {
            orderDlqUrl = getQueueUrl(orderDlqName);
        }
        return orderDlqUrl;
    }

    /**
     * Gets or creates the queue URL for a given queue name.
     *
     * @param queueName the name of the queue
     * @return queue URL
     */
    private String getQueueUrl(String queueName) {
        try {
            GetQueueUrlRequest getQueueUrlRequest = GetQueueUrlRequest.builder()
                    .queueName(queueName)
                    .build();

            GetQueueUrlResponse response = sqsClient.getQueueUrl(getQueueUrlRequest);
            return response.queueUrl();

        } catch (QueueDoesNotExistException e) {
            log.info("Queue {} does not exist. Creating it...", queueName);
            return createQueue(queueName);
        }
    }

    /**
     * Creates an SQS queue.
     *
     * @param queueName the name of the queue to create
     * @return queue URL
     */
    private String createQueue(String queueName) {
        try {
            CreateQueueRequest createQueueRequest = CreateQueueRequest.builder()
                    .queueName(queueName)
                    .build();

            CreateQueueResponse response = sqsClient.createQueue(createQueueRequest);
            log.info("Created SQS queue: {} with URL: {}", queueName, response.queueUrl());

            return response.queueUrl();

        } catch (Exception e) {
            log.error("Failed to create SQS queue: {}", queueName, e);
            throw new RuntimeException("Failed to create SQS queue", e);
        }
    }

    /**
     * Ensures the main queue and DLQ exist.
     * Creates them if they don't exist.
     */
    private void ensureQueueExists() {
        try {
            // Create DLQ first
            getOrderDlqUrl();

            // Then create main queue with DLQ configuration
            String mainQueueUrl = getOrderQueueUrl();
            String dlqArn = getQueueArn(getOrderDlqUrl());

            // Configure redrive policy
            String redrivePolicy = String.format(
                    "{\"deadLetterTargetArn\":\"%s\",\"maxReceiveCount\":\"3\"}",
                    dlqArn);

            SetQueueAttributesRequest setAttributesRequest = SetQueueAttributesRequest.builder()
                    .queueUrl(mainQueueUrl)
                    .attributes(java.util.Map.of(
                            QueueAttributeName.REDRIVE_POLICY, redrivePolicy,
                            QueueAttributeName.VISIBILITY_TIMEOUT, "300" // 5 minutes
                    ))
                    .build();

            sqsClient.setQueueAttributes(setAttributesRequest);
            log.info("Configured queue {} with DLQ", orderQueueName);

        } catch (Exception e) {
            log.warn("Failed to configure queue attributes: {}", e.getMessage());
        }
    }

    /**
     * Gets the ARN of a queue from its URL.
     *
     * @param queueUrl the queue URL
     * @return queue ARN
     */
    private String getQueueArn(String queueUrl) {
        GetQueueAttributesRequest request = GetQueueAttributesRequest.builder()
                .queueUrl(queueUrl)
                .attributeNames(QueueAttributeName.QUEUE_ARN)
                .build();

        GetQueueAttributesResponse response = sqsClient.getQueueAttributes(request);
        return response.attributes().get(QueueAttributeName.QUEUE_ARN);
    }
}
