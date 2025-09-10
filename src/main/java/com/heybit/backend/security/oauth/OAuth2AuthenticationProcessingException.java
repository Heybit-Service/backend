package com.heybit.backend.security.oauth;

import com.heybit.backend.global.exception.ErrorCode;
import org.springframework.security.core.AuthenticationException;

public class OAuth2AuthenticationProcessingException extends AuthenticationException {
  private final ErrorCode errorCode;

  public OAuth2AuthenticationProcessingException(ErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
  }

  public ErrorCode getErrorCode() {
    return errorCode;
  }
}
