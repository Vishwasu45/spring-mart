package com.springmart.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sqs.SqsClient;

import java.net.URI;

@Configuration
@Slf4j
public class AwsConfig {

    @Value("${app.aws.region}")
    private String region;

    @Value("${app.aws.endpoint:}")
    private String endpoint;

    @Value("${app.aws.access-key:}")
    private String accessKey;

    @Value("${app.aws.secret-key:}")
    private String secretKey;

    @Value("${app.aws.use-localstack:false}")
    private boolean useLocalStack;

    @Bean
    public S3Client s3Client() {
        var builder = S3Client.builder()
                .region(Region.of(region));

        if (useLocalStack && !endpoint.isEmpty()) {
            log.info("Using LocalStack for S3 at: {}", endpoint);
            builder.endpointOverride(URI.create(endpoint))
                    .credentialsProvider(localStackCredentials())
                    .forcePathStyle(true); // Required for LocalStack
        } else if (!accessKey.isEmpty() && !secretKey.isEmpty()) {
            builder.credentialsProvider(awsCredentials());
        }

        return builder.build();
    }

    @Bean
    public SqsClient sqsClient() {
        var builder = SqsClient.builder()
                .region(Region.of(region));

        if (useLocalStack && !endpoint.isEmpty()) {
            log.info("Using LocalStack for SQS at: {}", endpoint);
            builder.endpointOverride(URI.create(endpoint))
                    .credentialsProvider(localStackCredentials());
        } else if (!accessKey.isEmpty() && !secretKey.isEmpty()) {
            builder.credentialsProvider(awsCredentials());
        }

        return builder.build();
    }

    @Bean
    public SnsClient snsClient() {
        var builder = SnsClient.builder()
                .region(Region.of(region));

        if (useLocalStack && !endpoint.isEmpty()) {
            log.info("Using LocalStack for SNS at: {}", endpoint);
            builder.endpointOverride(URI.create(endpoint))
                    .credentialsProvider(localStackCredentials());
        } else if (!accessKey.isEmpty() && !secretKey.isEmpty()) {
            builder.credentialsProvider(awsCredentials());
        }

        return builder.build();
    }

    private AwsCredentialsProvider awsCredentials() {
        return StaticCredentialsProvider.create(
                AwsBasicCredentials.create(accessKey, secretKey));
    }

    private AwsCredentialsProvider localStackCredentials() {
        return StaticCredentialsProvider.create(
                AwsBasicCredentials.create("test", "test"));
    }
}
