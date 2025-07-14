package com.heybit.backend.infrastructure.s3;

import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@Slf4j
public class S3UploadComponent {

  public String upload(MultipartFile multipartFile, Long userCd) throws IOException {

    // TODO: S3 업로드 로직 직성
    log.info("upload file");

    return null;
  }
}
