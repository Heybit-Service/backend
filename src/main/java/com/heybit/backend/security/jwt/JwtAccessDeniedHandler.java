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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public void handle(HttpServletRequest request,
      HttpServletResponse response,
      AccessDeniedException accessDeniedException) throws IOException, ServletException {
    
    log.warn("Access denied: {} {}", request.getMethod(), request.getRequestURI());
    
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setCharacterEncoding("UTF-8");
    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
    
    Map<String, Object> errorResponse = new HashMap<>();
    errorResponse.put("success", false);
    errorResponse.put("error", "FORBIDDEN");
    errorResponse.put("message", "접근 권한이 없습니다.");
    
    response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
  }
}