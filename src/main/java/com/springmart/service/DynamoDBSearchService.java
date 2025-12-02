package com.springmart.service;

import com.springmart.entity.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for managing product search using DynamoDB.
 * Provides fast search capabilities for products.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DynamoDBSearchService {

    private final DynamoDbClient dynamoDbClient;

    @Value("${app.aws.dynamodb.product-search-table}")
    private String productTableName;

    /**
     * Indexes a product in DynamoDB for searching.
     *
     * @param product the product to index
     */
    public void indexProduct(Product product) {
        try {
            ensureTableExists();

            Map<String, AttributeValue> item = new HashMap<>();
            item.put("productId", AttributeValue.builder().n(product.getId().toString()).build());
            item.put("name", AttributeValue.builder().s(product.getName()).build());
            item.put("nameLower", AttributeValue.builder().s(product.getName().toLowerCase()).build());
            item.put("description", AttributeValue.builder()
                    .s(product.getDescription() != null ? product.getDescription() : "").build());
            item.put("price", AttributeValue.builder().n(product.getPrice().toString()).build());
            item.put("categoryId", AttributeValue.builder().n(product.getCategory().getId().toString()).build());
            item.put("categoryName", AttributeValue.builder().s(product.getCategory().getName()).build());
            item.put("stockQuantity", AttributeValue.builder().n(String.valueOf(product.getStockQuantity())).build());
            item.put("timestamp", AttributeValue.builder().n(String.valueOf(System.currentTimeMillis())).build());

            PutItemRequest putRequest = PutItemRequest.builder()
                    .tableName(productTableName)
                    .item(item)
                    .build();

            dynamoDbClient.putItem(putRequest);
            log.debug("Indexed product {} in DynamoDB", product.getId());

        } catch (Exception e) {
            log.error("Failed to index product {} in DynamoDB", product.getId(), e);
        }
    }

    /**
     * Searches for products by name or description.
     *
     * @param query the search query
     * @return list of matching product IDs
     */
    public List<Long> searchProducts(String query) {
        try {
            ensureTableExists();

            String queryLower = query.toLowerCase();

            // Use scan for full-text search (in production, consider using ElasticSearch)
            Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
            expressionAttributeValues.put(":query", AttributeValue.builder().s(queryLower).build());

            ScanRequest scanRequest = ScanRequest.builder()
                    .tableName(productTableName)
                    .filterExpression("contains(nameLower, :query) OR contains(description, :query)")
                    .expressionAttributeValues(expressionAttributeValues)
                    .build();

            ScanResponse response = dynamoDbClient.scan(scanRequest);

            return response.items().stream()
                    .map(item -> Long.parseLong(item.get("productId").n()))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Failed to search products with query: {}", query, e);
            return Collections.emptyList();
        }
    }

    /**
     * Searches for products by category.
     *
     * @param categoryId the category ID
     * @return list of matching product IDs
     */
    public List<Long> searchByCategory(Long categoryId) {
        try {
            ensureTableExists();

            Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
            expressionAttributeValues.put(":catId", AttributeValue.builder().n(categoryId.toString()).build());

            ScanRequest scanRequest = ScanRequest.builder()
                    .tableName(productTableName)
                    .filterExpression("categoryId = :catId")
                    .expressionAttributeValues(expressionAttributeValues)
                    .build();

            ScanResponse response = dynamoDbClient.scan(scanRequest);

            return response.items().stream()
                    .map(item -> Long.parseLong(item.get("productId").n()))
                    .sorted()
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Failed to search products by category: {}", categoryId, e);
            return Collections.emptyList();
        }
    }

    /**
     * Deletes a product from the search index.
     *
     * @param productId the product ID to delete
     */
    public void deleteProduct(Long productId) {
        try {
            Map<String, AttributeValue> key = new HashMap<>();
            key.put("productId", AttributeValue.builder().n(productId.toString()).build());

            DeleteItemRequest deleteRequest = DeleteItemRequest.builder()
                    .tableName(productTableName)
                    .key(key)
                    .build();

            dynamoDbClient.deleteItem(deleteRequest);
            log.debug("Deleted product {} from DynamoDB index", productId);

        } catch (Exception e) {
            log.error("Failed to delete product {} from DynamoDB", productId, e);
        }
    }

    /**
     * Ensures the DynamoDB table exists, creating it if necessary.
     */
    private void ensureTableExists() {
        try {
            DescribeTableRequest describeRequest = DescribeTableRequest.builder()
                    .tableName(productTableName)
                    .build();

            dynamoDbClient.describeTable(describeRequest);
            log.debug("DynamoDB table {} exists", productTableName);

        } catch (ResourceNotFoundException e) {
            log.info("DynamoDB table {} does not exist. Creating it...", productTableName);
            createTable();
        }
    }

    /**
     * Creates the DynamoDB table for product search.
     */
    private void createTable() {
        try {
            CreateTableRequest createRequest = CreateTableRequest.builder()
                    .tableName(productTableName)
                    .keySchema(
                            KeySchemaElement.builder()
                                    .attributeName("productId")
                                    .keyType(KeyType.HASH)
                                    .build())
                    .attributeDefinitions(
                            AttributeDefinition.builder()
                                    .attributeName("productId")
                                    .attributeType(ScalarAttributeType.N)
                                    .build())
                    .billingMode(BillingMode.PAY_PER_REQUEST) // On-demand pricing
                    .build();

            dynamoDbClient.createTable(createRequest);
            log.info("Created DynamoDB table: {}", productTableName);

            // Wait for table to become active
            waitForTableActive();

        } catch (Exception e) {
            log.error("Failed to create DynamoDB table", e);
        }
    }

    /**
     * Waits for the table to become active.
     */
    private void waitForTableActive() {
        try {
            Thread.sleep(2000); // Simple wait for LocalStack
            log.info("DynamoDB table {} is now active", productTableName);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Re-indexes all products from the provided list.
     *
     * @param products list of products to index
     */
    public void reindexAllProducts(List<Product> products) {
        log.info("Re-indexing {} products in DynamoDB", products.size());

        for (Product product : products) {
            indexProduct(product);
        }

        log.info("Re-indexing complete");
    }
}
