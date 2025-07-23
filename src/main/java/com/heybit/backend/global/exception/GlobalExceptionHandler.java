package com.heybit.backend.global.exception;

import com.heybit.backend.global.response.ApiResponseEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  @ExceptionHandler(ApiException.class)
  public ResponseEntity<ApiResponseEntity<ErrorResponse>> handleApiException(ApiException e) {
    ErrorCode errorCode = e.getErrorCode();
    ErrorResponse errorResponse = ErrorResponse.from(errorCode);
    return ResponseEntity.status(errorResponse.getStatus())
        .body(ApiResponseEntity.error(
            errorResponse.getStatus(),
            errorResponse.getMessage()
        ));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResponseEntity<ErrorResponse>> handleException(Exception e) {

    log.error("Exception occurred: {}", e.toString());

    ErrorResponse errorResponse = ErrorResponse.from(ErrorCode.INTERNAL_SERVER_ERROR);

    return ResponseEntity.status(errorResponse.getStatus())
        .body(ApiResponseEntity.error(
            ErrorCode.INTERNAL_SERVER_ERROR.getStatus().value(),
            errorResponse.getMessage()
        ));
  }

}
