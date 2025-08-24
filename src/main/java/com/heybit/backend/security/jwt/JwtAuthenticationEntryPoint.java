package com.heybit.backend.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public void commence(HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException authException) throws IOException, ServletException {
    
    log.warn("Unauthorized access attempt: {} {}", request.getMethod(), request.getRequestURI());
    
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setCharacterEncoding("UTF-8");
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    
    Map<String, Object> errorResponse = new HashMap<>();
    errorResponse.put("success", false);
    errorResponse.put("error", "UNAUTHORIZED");
    errorResponse.put("message", "인증이 필요합니다. 유효한 토큰을 제공해주세요.");
    
    response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
  }
}