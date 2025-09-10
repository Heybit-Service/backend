package com.heybit.backend.security.oauth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.heybit.backend.global.exception.ApiException;
import com.heybit.backend.global.exception.ErrorCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

@Component
public class OAuth2LoginFailureHandler implements AuthenticationFailureHandler {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public void onAuthenticationFailure(HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException exception) throws IOException, ServletException {

    int status = 401;
    String error = "UNAUTHORIZED";
    String message = "인증이 필요합니다. 유효한 토큰을 제공해주세요.";

    if (exception instanceof OAuth2AuthenticationProcessingException ex) {
      status = ex.getErrorCode().getStatus().value();
      error = ex.getErrorCode().name();
      message = ex.getErrorCode().getMessage();
    }

    response.setStatus(status);
    response.setContentType("application/json;charset=UTF-8");

    Map<String, Object> body = new HashMap<>();
    body.put("success", false);
    body.put("error", error);
    body.put("message", message);

    response.getWriter().write(objectMapper.writeValueAsString(body));
  }
}
