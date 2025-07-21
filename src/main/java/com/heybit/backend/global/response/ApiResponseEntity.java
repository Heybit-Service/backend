package com.heybit.backend.global.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public class ApiResponseEntity<T> {

  private boolean success;
  private int status;
  private String message;
  private T data;

  public static <T> ApiResponseEntity<T> success() {
    return new ApiResponseEntity<>(
        true,
        HttpStatus.OK.value(),
        "success",
        null
    );
  }

  public static <T> ApiResponseEntity<T> success(T data) {
    return new ApiResponseEntity<>(
        true,
        HttpStatus.OK.value(),
        "success",
        data
    );
  }

  public static <T> ApiResponseEntity<T> error(int status, String message) {
    return new ApiResponseEntity<>(
        false,
        status,
        message,
        null
    );
  }

  @Override
  public String toString() {
    return "ApiResponseEntity{" +
        "success=" + success +
        ", status=" + status +
        ", message='" + message + '\'' +
        ", data=" + data +
        '}';
  }

}
