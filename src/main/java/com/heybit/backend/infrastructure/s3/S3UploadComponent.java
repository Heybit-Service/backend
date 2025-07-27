package com.heybit.backend.infrastructure.s3;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Delete;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Object;

@Component
@Slf4j
@RequiredArgsConstructor
public class S3UploadComponent {

  private static final String PRODUCT_DIR = "product-images";
  private static final String DEFAULT_CONTENT_TYPE = "application/octet-stream";

  private final S3Client s3Client;

  @Value("${cloud.aws.s3.bucket}")
  private String bucket;

  public String upload(MultipartFile file, Long userId) throws IOException {

    log.info("Start upload file");

    String key = buildFileKey(userId, file);

    PutObjectRequest request = PutObjectRequest.builder()
        .bucket(bucket)
        .key(key)
        .contentType(Optional.ofNullable(file.getContentType()).orElse(DEFAULT_CONTENT_TYPE))
        .build();

    s3Client.putObject(request, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

    log.info("Successfully uploaded file: key={}", key);
    return generateUrl(key);
  }

  public void delete(String key) {
    DeleteObjectRequest request = DeleteObjectRequest.builder()
        .bucket(bucket)
        .key(key)
        .build();

    s3Client.deleteObject(request);
    log.info("Successfully deleted file: key={}", key);
  }

  public void deleteAllByUserId(Long userId) {
    String prefix = userId + "/";
    List<ObjectIdentifier> keysToDelete = getAllObjectIdentifiersByPrefix(prefix);

    if (!CollectionUtils.isEmpty(keysToDelete)) {
      deleteObjects(keysToDelete);
    }
  }

  private String buildFileKey(Long userId, MultipartFile file) {
    String originalFilename = Optional.ofNullable(file.getOriginalFilename()).orElse("file");
    String ext = "";

    int idx = originalFilename.lastIndexOf(".");
    if (idx != -1) {
      ext = originalFilename.substring(idx);
    }

    String uuid = UUID.randomUUID().toString();

    return String.format("%d/%s/product-%s%s", userId, PRODUCT_DIR, uuid, ext.toLowerCase());
  }

  private String generateUrl(String key) {
    return "https://" + bucket + ".s3.amazonaws.com/" + key;
  }

  private List<ObjectIdentifier> getAllObjectIdentifiersByPrefix(String prefix) {
    ListObjectsV2Request request = ListObjectsV2Request.builder()
        .bucket(bucket)
        .prefix(prefix)
        .build();

    return Optional.ofNullable(s3Client.listObjectsV2(request).contents())
        .orElse(List.of())
        .stream()
        .map(S3Object::key)
        .map(key -> ObjectIdentifier.builder().key(key).build())
        .collect(Collectors.toList());
  }

  private void deleteObjects(List<ObjectIdentifier> identifiers) {
    DeleteObjectsRequest deleteRequest = DeleteObjectsRequest.builder()
        .bucket(bucket)
        .delete(Delete.builder().objects(identifiers).build())
        .build();

    s3Client.deleteObjects(deleteRequest);
  }
}
