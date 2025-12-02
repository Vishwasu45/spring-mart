package com.springmart.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.InputStream;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Service for managing file uploads to Amazon S3.
 * Handles product images and other file storage operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service {

    private final S3Client s3Client;

    @Value("${app.aws.s3.image-bucket}")
    private String imageBucket;

    /**
     * Uploads a file to S3 and returns the S3 key.
     *
     * @param inputStream the file input stream
     * @param fileName    original file name
     * @param contentType MIME type of the file
     * @return S3 key (path) of the uploaded file
     */
    public String uploadFile(InputStream inputStream, String fileName, String contentType) {
        try {
            // Generate unique file name
            String fileExtension = getFileExtension(fileName);
            String s3Key = generateUniqueKey(fileExtension);

            // Create bucket if it doesn't exist (useful for LocalStack)
            createBucketIfNotExists();

            // Upload file to S3
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(imageBucket)
                    .key(s3Key)
                    .contentType(contentType)
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, inputStream.available()));

            log.info("Successfully uploaded file to S3: {}", s3Key);
            return s3Key;

        } catch (Exception e) {
            log.error("Error uploading file to S3", e);
            throw new RuntimeException("Failed to upload file to S3", e);
        }
    }

    /**
     * Generates a presigned URL for accessing an S3 object.
     * URL is valid for 1 hour.
     *
     * @param s3Key the S3 key of the object
     * @return presigned URL as a string
     */
    public String generatePresignedUrl(String s3Key) {
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(imageBucket)
                    .key(s3Key)
                    .build();

            // For LocalStack, we can use a simpler URL structure
            // In production, use S3Presigner for secure presigned URLs
            if (isLocalStack()) {
                return String.format("http://localhost:4566/%s/%s", imageBucket, s3Key);
            }

            // Production: Use presigned URLs
            try (S3Presigner presigner = S3Presigner.create()) {
                GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                        .signatureDuration(Duration.ofHours(1))
                        .getObjectRequest(getObjectRequest)
                        .build();

                PresignedGetObjectRequest presignedRequest = presigner.presignGetObject(presignRequest);
                return presignedRequest.url().toString();
            }

        } catch (Exception e) {
            log.error("Error generating presigned URL for key: {}", s3Key, e);
            throw new RuntimeException("Failed to generate presigned URL", e);
        }
    }

    /**
     * Deletes a file from S3.
     *
     * @param s3Key the S3 key of the file to delete
     */
    public void deleteFile(String s3Key) {
        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(imageBucket)
                    .key(s3Key)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);
            log.info("Successfully deleted file from S3: {}", s3Key);

        } catch (Exception e) {
            log.error("Error deleting file from S3: {}", s3Key, e);
            throw new RuntimeException("Failed to delete file from S3", e);
        }
    }

    /**
     * Deletes multiple files from S3.
     *
     * @param s3Keys list of S3 keys to delete
     */
    public void deleteFiles(List<String> s3Keys) {
        if (s3Keys == null || s3Keys.isEmpty()) {
            return;
        }

        try {
            List<ObjectIdentifier> objectIdentifiers = s3Keys.stream()
                    .map(key -> ObjectIdentifier.builder().key(key).build())
                    .toList();

            Delete delete = Delete.builder()
                    .objects(objectIdentifiers)
                    .build();

            DeleteObjectsRequest deleteObjectsRequest = DeleteObjectsRequest.builder()
                    .bucket(imageBucket)
                    .delete(delete)
                    .build();

            s3Client.deleteObjects(deleteObjectsRequest);
            log.info("Successfully deleted {} files from S3", s3Keys.size());

        } catch (Exception e) {
            log.error("Error deleting files from S3", e);
            throw new RuntimeException("Failed to delete files from S3", e);
        }
    }

    /**
     * Lists all files in the bucket with a specific prefix.
     *
     * @param prefix the prefix to filter by (folder path)
     * @return list of S3 keys
     */
    public List<String> listFiles(String prefix) {
        try {
            ListObjectsV2Request listRequest = ListObjectsV2Request.builder()
                    .bucket(imageBucket)
                    .prefix(prefix)
                    .build();

            ListObjectsV2Response listResponse = s3Client.listObjectsV2(listRequest);

            List<String> keys = new ArrayList<>();
            for (S3Object s3Object : listResponse.contents()) {
                keys.add(s3Object.key());
            }

            return keys;

        } catch (Exception e) {
            log.error("Error listing files from S3", e);
            throw new RuntimeException("Failed to list files from S3", e);
        }
    }

    /**
     * Creates the S3 bucket if it doesn't exist.
     * Useful for LocalStack where buckets need to be created.
     */
    private void createBucketIfNotExists() {
        try {
            HeadBucketRequest headBucketRequest = HeadBucketRequest.builder()
                    .bucket(imageBucket)
                    .build();

            s3Client.headBucket(headBucketRequest);
            log.debug("Bucket {} already exists", imageBucket);

        } catch (NoSuchBucketException e) {
            log.info("Bucket {} does not exist. Creating it...", imageBucket);

            CreateBucketRequest createBucketRequest = CreateBucketRequest.builder()
                    .bucket(imageBucket)
                    .build();

            s3Client.createBucket(createBucketRequest);
            log.info("Successfully created bucket: {}", imageBucket);

        } catch (Exception e) {
            log.warn("Error checking bucket existence: {}", e.getMessage());
        }
    }

    /**
     * Generates a unique key for storing files in S3.
     *
     * @param fileExtension the file extension
     * @return unique S3 key
     */
    private String generateUniqueKey(String fileExtension) {
        String uuid = UUID.randomUUID().toString();
        return "products/" + uuid + fileExtension;
    }

    /**
     * Extracts file extension from filename.
     *
     * @param fileName the original filename
     * @return file extension with dot (e.g., ".jpg")
     */
    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf("."));
    }

    /**
     * Checks if we're running against LocalStack.
     *
     * @return true if using LocalStack
     */
    private boolean isLocalStack() {
        // Simple heuristic: if the S3 endpoint contains localhost, we're using
        // LocalStack
        try {
            return s3Client.serviceClientConfiguration().endpointOverride()
                    .map(uri -> uri.toString().contains("localhost"))
                    .orElse(false);
        } catch (Exception e) {
            return false;
        }
    }
}
