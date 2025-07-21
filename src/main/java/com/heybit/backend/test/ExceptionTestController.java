package com.heybit.backend.test;

import com.heybit.backend.global.exception.ApiException;
import com.heybit.backend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class ExceptionTestController {

  @GetMapping("/api-exception")
  public String throwApiException() {
    throw new ApiException(ErrorCode.INVALID_REQUEST);
  }

  @GetMapping("/runtime-exception")
  public String throwRuntimeException() {
    throw new IllegalArgumentException("잘못된 입력입니다.");
  }
}
