package com.heybit.backend.security.oauth;

import com.heybit.backend.security.jwt.JwtTokenProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.Base64;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2LoginSuccessHandler implements
    AuthenticationSuccessHandler {

  @Value("${app.frontend.base-url}")
  private String frontendBaseUrl;
  
  @Value("${app.cors.allowed-origins}")
  private String allowedOrigins;

  private final JwtTokenProvider jwtTokenProvider;

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException, ServletException {
    CustomOAuth2User userPrincipal = (CustomOAuth2User) authentication.getPrincipal();
    String jwt = jwtTokenProvider.createAccessToken(userPrincipal);

    String nickname = userPrincipal.getNickname();
    
    // Extract origin from state parameter
    String baseUrl = extractOriginFromState(request);
    if (baseUrl == null || !isAllowedOrigin(baseUrl)) {
      baseUrl = frontendBaseUrl;
    }

    // TODO:  현재는 JWT를 직접 URL에 노출
    //        추후 One-Time Token(OTT) 발급 → JWT 조회 방식으로 리팩토링 예정
    // TODO: 프론트와 리디렉션 URL 구조 확정 시 반영 예정(1)
    String targetUrl;
    if (nickname == null || nickname.isEmpty()) {
      targetUrl = baseUrl + "/register?token=" + jwt;
    } else {
      targetUrl = baseUrl + "/dashboard/timer/progress?token=" + jwt;
    }

    log.info("[OAuth2 Success] Redirect URI = {}", targetUrl);

    clearAuthenticationAttributes(request);

    response.sendRedirect(targetUrl);
  }

  protected void clearAuthenticationAttributes(HttpServletRequest request) {
    var session = request.getSession(false);
    if (session == null) {
      return;
    }
    session.removeAttribute("SPRING_SECURITY_LAST_EXCEPTION");
  }
  
  private String extractOriginFromState(HttpServletRequest request) {
    String state = request.getParameter("state");
    if (state == null || state.isEmpty()) {
      return null;
    }
    
    try {
      String decodedState = new String(Base64.getUrlDecoder().decode(state));
      String[] parts = decodedState.split("\\|");
      if (parts.length >= 2) {
        return parts[1];
      }
    } catch (Exception e) {
      log.warn("Failed to extract origin from state parameter", e);
    }
    
    return null;
  }
  
  private boolean isAllowedOrigin(String origin) {
    if (allowedOrigins == null || origin == null) {
      return false;
    }
    
    Set<String> allowedSet = Set.of(allowedOrigins.split(","));
    return allowedSet.stream()
        .map(String::trim)
        .anyMatch(allowed -> allowed.equals(origin));
  }
}
