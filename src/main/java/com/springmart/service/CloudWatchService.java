package com.springmart.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.cloudwatch.CloudWatchClient;
import software.amazon.awssdk.services.cloudwatch.model.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for pushing custom metrics to CloudWatch.
 * Monitors application performance and business metrics.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CloudWatchService {

    private final CloudWatchClient cloudWatchClient;

    @Value("${app.aws.cloudwatch.namespace}")
    private String namespace;

    /**
     * Records an order placed metric.
     *
     * @param amount the order amount
     */
    public void recordOrderPlaced(Double amount) {
        putMetric("OrdersPlaced", 1.0, StandardUnit.COUNT);
        putMetric("Revenue", amount, StandardUnit.NONE);
    }

    /**
     * Records an order completed metric.
     */
    public void recordOrderCompleted() {
        putMetric("OrdersCompleted", 1.0, StandardUnit.COUNT);
    }

    /**
     * Records a product view metric.
     *
     * @param productId the product ID that was viewed
     */
    public void recordProductView(Long productId) {
        putMetricWithDimension("ProductViews", 1.0, StandardUnit.COUNT,
                "ProductId", productId.toString());
    }

    /**
     * Records a cart add metric.
     */
    public void recordCartAdd() {
        putMetric("CartAdds", 1.0, StandardUnit.COUNT);
    }

    /**
     * Records an API error metric.
     *
     * @param endpoint the endpoint that errored
     */
    public void recordApiError(String endpoint) {
        putMetricWithDimension("ApiErrors", 1.0, StandardUnit.COUNT,
                "Endpoint", endpoint);
    }

    /**
     * Records API latency.
     *
     * @param endpoint  the endpoint
     * @param latencyMs the latency in milliseconds
     */
    public void recordApiLatency(String endpoint, long latencyMs) {
        putMetricWithDimension("ApiLatency", (double) latencyMs, StandardUnit.MILLISECONDS,
                "Endpoint", endpoint);
    }

    /**
     * Puts a simple metric to CloudWatch.
     *
     * @param metricName the metric name
     * @param value      the metric value
     * @param unit       the unit of measurement
     */
    private void putMetric(String metricName, Double value, StandardUnit unit) {
        putMetricWithDimensions(metricName, value, unit, new ArrayList<>());
    }

    /**
     * Puts a metric with a single dimension.
     *
     * @param metricName     the metric name
     * @param value          the metric value
     * @param unit           the unit of measurement
     * @param dimensionName  the dimension name
     * @param dimensionValue the dimension value
     */
    private void putMetricWithDimension(String metricName, Double value, StandardUnit unit,
            String dimensionName, String dimensionValue) {
        List<Dimension> dimensions = List.of(
                Dimension.builder()
                        .name(dimensionName)
                        .value(dimensionValue)
                        .build());
        putMetricWithDimensions(metricName, value, unit, dimensions);
    }

    /**
     * Puts a metric with multiple dimensions.
     *
     * @param metricName the metric name
     * @param value      the metric value
     * @param unit       the unit of measurement
     * @param dimensions the dimensions
     */
    private void putMetricWithDimensions(String metricName, Double value, StandardUnit unit,
            List<Dimension> dimensions) {
        try {
            MetricDatum metricDatum = MetricDatum.builder()
                    .metricName(metricName)
                    .value(value)
                    .unit(unit)
                    .timestamp(Instant.now())
                    .dimensions(dimensions)
                    .build();

            PutMetricDataRequest request = PutMetricDataRequest.builder()
                    .namespace(namespace)
                    .metricData(metricDatum)
                    .build();

            cloudWatchClient.putMetricData(request);
            log.debug("Sent metric to CloudWatch: {} = {}", metricName, value);

        } catch (Exception e) {
            log.warn("Failed to send metric to CloudWatch: {}", metricName, e);
            // Don't throw - metrics should not break the application
        }
    }

    /**
     * Records multiple metrics in a batch.
     *
     * @param metrics list of metric data
     */
    public void putMetrics(List<MetricDatum> metrics) {
        try {
            // CloudWatch allows max 1000 metrics per request, but we'll use smaller batches
            int batchSize = 20;
            for (int i = 0; i < metrics.size(); i += batchSize) {
                int end = Math.min(i + batchSize, metrics.size());
                List<MetricDatum> batch = metrics.subList(i, end);

                PutMetricDataRequest request = PutMetricDataRequest.builder()
                        .namespace(namespace)
                        .metricData(batch)
                        .build();

                cloudWatchClient.putMetricData(request);
            }

            log.debug("Sent {} metrics to CloudWatch", metrics.size());

        } catch (Exception e) {
            log.warn("Failed to send batch metrics to CloudWatch", e);
        }
    }
}
