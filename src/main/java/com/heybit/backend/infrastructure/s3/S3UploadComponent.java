package com.heybit.backend.infrastructure.s3;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Component
@Slf4j
@RequiredArgsConstructor
public class S3UploadComponent {

  private static final String PRODUCT_DIR = "product-images";

  private final S3Client s3Client;

  @Value("${cloud.aws.s3.bucket}")
  private String bucket;

  public String upload(MultipartFile file, Long userId) throws IOException {

    log.info("Start upload file");

    String key = buildFileKey(userId, file);

    PutObjectRequest request = PutObjectRequest.builder()
        .bucket(bucket)
        .key(key)
        .contentType(file.getContentType())
        .build();

    s3Client.putObject(request, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

    log.info("Successfully uploaded file: key={}", key);
    return generateUrl(key);
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

  public void delete(String imageUrl) {
    // TODO: S3 업로드 삭제 로직 직성
    log.info("delete file");
  }
}
