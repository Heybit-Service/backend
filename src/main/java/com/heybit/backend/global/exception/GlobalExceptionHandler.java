package com.heybit.backend.global.exception;

import com.heybit.backend.global.response.ApiResponseEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

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

  // JSON 파싱 실패
  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ApiResponseEntity<ErrorResponse>> handleHttpMessageNotReadable(HttpMessageNotReadableException e) {
    ErrorResponse errorResponse = ErrorResponse.from(ErrorCode.MALFORMED_JSON);
    return ResponseEntity.status(errorResponse.getStatus())
        .body(ApiResponseEntity.error(
            errorResponse.getStatus(),
            errorResponse.getMessage()
        ));
  }

  // PathVariable, RequestParam 타입 불일치
  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ApiResponseEntity<ErrorResponse>> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
    ErrorResponse errorResponse = ErrorResponse.from(ErrorCode.TYPE_MISMATCH);
    String message = String.format("Parameter '%s' should be of type '%s'", e.getName(), e.getRequiredType().getSimpleName());
    return ResponseEntity.status(errorResponse.getStatus())
        .body(ApiResponseEntity.error(errorResponse.getStatus(), message));
  }

  //필수 파라미터 누락
  @ExceptionHandler(MissingServletRequestParameterException.class)
  public ResponseEntity<ApiResponseEntity<ErrorResponse>> handleMissingParam(
      MissingServletRequestParameterException e) {
    ErrorResponse errorResponse = ErrorResponse.from(ErrorCode.MISSING_PARAMETER);
    String message = String.format("Required parameter '%s' is missing", e.getParameterName());
    return ResponseEntity.status(errorResponse.getStatus())
        .body(ApiResponseEntity.error(errorResponse.getStatus(), message));
  }

  // Validation 실패
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiResponseEntity<ErrorResponse>> handleValidationExceptions(MethodArgumentNotValidException e) {
    StringBuilder errors = new StringBuilder();
    for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
      errors.append(fieldError.getField())
          .append(": ")
          .append(fieldError.getDefaultMessage())
          .append("; ");
    }
    ErrorResponse errorResponse = ErrorResponse.from(ErrorCode.VALIDATION_FAILED);
    return ResponseEntity.status(errorResponse.getStatus())
        .body(ApiResponseEntity.error(errorResponse.getStatus(), errors.toString()));
  }

  // 404 Not Found
  @ExceptionHandler(NoHandlerFoundException.class)
  public ResponseEntity<ApiResponseEntity<ErrorResponse>> handleNotFound(NoHandlerFoundException e) {
    ErrorResponse errorResponse = ErrorResponse.from(ErrorCode.API_NOT_FOUND);
    return ResponseEntity.status(errorResponse.getStatus())
        .body(ApiResponseEntity.error(errorResponse.getStatus(), errorResponse.getMessage()));
  }

  // 지원되지 않는 Content-Type
  @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
  public ResponseEntity<ApiResponseEntity<ErrorResponse>> handleMediaTypeNotSupported(HttpMediaTypeNotSupportedException e) {
    ErrorResponse errorResponse = ErrorResponse.from(ErrorCode.INVALID_REQUEST);
    String message = String.format("Content-Type '%s' is not supported. Supported types: %s",
        e.getContentType(), e.getSupportedMediaTypes());
    return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
        .body(ApiResponseEntity.error(errorResponse.getStatus(), message));
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
