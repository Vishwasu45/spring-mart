package com.springmart.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.*;

import java.util.Map;

/**
 * Service for managing SNS notifications.
 * Publishes messages to SNS topics for order events and notifications.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SNSService {

    private final SnsClient snsClient;

    @Value("${app.aws.sns.order-topic-arn}")
    private String orderTopicArn;

    private String actualTopicArn;

    /**
     * Publishes an order placed notification.
     *
     * @param orderId     the order ID
     * @param userEmail   the customer's email
     * @param totalAmount the total order amount
     */
    public void publishOrderPlaced(Long orderId, String userEmail, Double totalAmount) {
        String message = String.format(
                "New order #%d placed!\nCustomer: %s\nTotal: $%.2f",
                orderId, userEmail, totalAmount);

        publishMessage(
                "Order Placed",
                message,
                Map.of(
                        "orderId", orderId.toString(),
                        "userEmail", userEmail,
                        "amount", totalAmount.toString(),
                        "eventType", "ORDER_PLACED"));
    }

    /**
     * Publishes an order shipped notification.
     *
     * @param orderId        the order ID
     * @param userEmail      the customer's email
     * @param trackingNumber the shipping tracking number
     */
    public void publishOrderShipped(Long orderId, String userEmail, String trackingNumber) {
        String message = String.format(
                "Order #%d has been shipped!\nTracking Number: %s",
                orderId, trackingNumber);

        publishMessage(
                "Order Shipped",
                message,
                Map.of(
                        "orderId", orderId.toString(),
                        "userEmail", userEmail,
                        "trackingNumber", trackingNumber,
                        "eventType", "ORDER_SHIPPED"));
    }

    /**
     * Publishes an order delivered notification.
     *
     * @param orderId   the order ID
     * @param userEmail the customer's email
     */
    public void publishOrderDelivered(Long orderId, String userEmail) {
        String message = String.format(
                "Order #%d has been delivered!\nThank you for shopping with us.",
                orderId);

        publishMessage(
                "Order Delivered",
                message,
                Map.of(
                        "orderId", orderId.toString(),
                        "userEmail", userEmail,
                        "eventType", "ORDER_DELIVERED"));
    }

    /**
     * Publishes a generic message to the SNS topic.
     *
     * @param subject    the message subject
     * @param message    the message body
     * @param attributes message attributes for filtering
     */
    private void publishMessage(String subject, String message, Map<String, String> attributes) {
        try {
            ensureTopicExists();

            // Convert string attributes to SNS message attributes
            Map<String, MessageAttributeValue> messageAttributes = attributes.entrySet().stream()
                    .collect(java.util.stream.Collectors.toMap(
                            Map.Entry::getKey,
                            entry -> MessageAttributeValue.builder()
                                    .stringValue(entry.getValue())
                                    .dataType("String")
                                    .build()));

            PublishRequest publishRequest = PublishRequest.builder()
                    .topicArn(getActualTopicArn())
                    .subject(subject)
                    .message(message)
                    .messageAttributes(messageAttributes)
                    .build();

            PublishResponse response = snsClient.publish(publishRequest);
            log.info("Published SNS message: {} - Message ID: {}", subject, response.messageId());

        } catch (Exception e) {
            log.error("Failed to publish SNS message: {}", subject, e);
            // Don't throw exception - notifications should not break the main flow
        }
    }

    /**
     * Gets the actual topic ARN, ensuring the topic exists.
     *
     * @return topic ARN
     */
    private String getActualTopicArn() {
        if (actualTopicArn == null) {
            ensureTopicExists();
        }
        return actualTopicArn;
    }

    /**
     * Ensures the SNS topic exists, creating it if necessary.
     */
    private void ensureTopicExists() {
        try {
            // Extract topic name from ARN
            String topicName = extractTopicName(orderTopicArn);

            // Try to create the topic (idempotent operation)
            CreateTopicRequest createTopicRequest = CreateTopicRequest.builder()
                    .name(topicName)
                    .build();

            CreateTopicResponse response = snsClient.createTopic(createTopicRequest);
            actualTopicArn = response.topicArn();

            log.info("SNS topic ensured: {} with ARN: {}", topicName, actualTopicArn);

        } catch (Exception e) {
            log.warn("Failed to ensure SNS topic exists: {}", e.getMessage());
            // Use the configured ARN as fallback
            actualTopicArn = orderTopicArn;
        }
    }

    /**
     * Extracts topic name from ARN.
     * ARN format: arn:aws:sns:region:account-id:topic-name
     *
     * @param arn the topic ARN
     * @return topic name
     */
    private String extractTopicName(String arn) {
        if (arn.contains(":")) {
            String[] parts = arn.split(":");
            return parts[parts.length - 1];
        }
        return arn;
    }

    /**
     * Subscribes an email address to the order topic.
     *
     * @param email the email address to subscribe
     * @return subscription ARN
     */
    public String subscribeEmail(String email) {
        try {
            ensureTopicExists();

            SubscribeRequest subscribeRequest = SubscribeRequest.builder()
                    .topicArn(getActualTopicArn())
                    .protocol("email")
                    .endpoint(email)
                    .build();

            SubscribeResponse response = snsClient.subscribe(subscribeRequest);
            log.info("Subscribed email {} to SNS topic. Subscription ARN: {}", email, response.subscriptionArn());

            return response.subscriptionArn();

        } catch (Exception e) {
            log.error("Failed to subscribe email to SNS topic", e);
            throw new RuntimeException("Failed to subscribe email to notifications", e);
        }
    }

    /**
     * Unsubscribes an email address from the order topic.
     *
     * @param subscriptionArn the subscription ARN to unsubscribe
     */
    public void unsubscribeEmail(String subscriptionArn) {
        try {
            if (subscriptionArn == null || subscriptionArn.isEmpty()) {
                log.warn("Cannot unsubscribe: subscription ARN is null or empty");
                return;
            }

            UnsubscribeRequest unsubscribeRequest = UnsubscribeRequest.builder()
                    .subscriptionArn(subscriptionArn)
                    .build();

            snsClient.unsubscribe(unsubscribeRequest);
            log.info("Unsubscribed from SNS topic. Subscription ARN: {}", subscriptionArn);

        } catch (Exception e) {
            log.error("Failed to unsubscribe from SNS topic", e);
            throw new RuntimeException("Failed to unsubscribe from notifications", e);
        }
    }

    /**
     * Checks if a subscription is confirmed.
     *
     * @param subscriptionArn the subscription ARN to check
     * @return true if confirmed, false otherwise
     */
    public boolean isSubscriptionConfirmed(String subscriptionArn) {
        try {
            if (subscriptionArn == null || subscriptionArn.isEmpty()) {
                return false;
            }

            // List all subscriptions for the topic
            ListSubscriptionsByTopicRequest request = ListSubscriptionsByTopicRequest.builder()
                    .topicArn(getActualTopicArn())
                    .build();

            ListSubscriptionsByTopicResponse response = snsClient.listSubscriptionsByTopic(request);

            // Check if the subscription ARN exists and is confirmed
            return response.subscriptions().stream()
                    .anyMatch(sub -> sub.subscriptionArn().equals(subscriptionArn)
                            && !"PendingConfirmation".equals(sub.subscriptionArn()));

        } catch (Exception e) {
            log.error("Failed to check subscription status", e);
            return false;
        }
    }
}
