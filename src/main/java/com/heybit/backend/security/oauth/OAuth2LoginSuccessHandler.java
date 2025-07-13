package com.heybit.backend.security.oauth;

import com.heybit.backend.security.jwt.JwtTokenProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2LoginSuccessHandler implements
    AuthenticationSuccessHandler {

  private final JwtTokenProvider jwtTokenProvider;

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException, ServletException {
    CustomOAuth2User userPrincipal = (CustomOAuth2User) authentication.getPrincipal();
    String jwt = jwtTokenProvider.createAccessToken(userPrincipal);

    String nickname = userPrincipal.getNickname();

    // TODO:  현재는 JWT를 직접 URL에 노출
    //        추후 One-Time Token(OTT) 발급 → JWT 조회 방식으로 리팩토링 예정
    // TODO: 프론트와 리디렉션 URL 구조 확정 시 반영 예정(1)
    String targetUrl;
    if (nickname == null || nickname.isEmpty()) {
      targetUrl = "http://localhost:3000/nickname-setup?token=" + jwt;
    } else {
      targetUrl = "http://localhost:3000/main?token=" + jwt;
    }

    log.info("[OAuth2 Success] JWT Token = {}", jwt);
    log.info("[OAuth2 Success] Redirect URI = {}", targetUrl);

    clearAuthenticationAttributes(request);

    //TODO: 프론트와 리디렉션 URL 구조 확정 시 반영 예정(2)
//    response.sendRedirect(targetUrl);
  }

  protected void clearAuthenticationAttributes(HttpServletRequest request) {
    var session = request.getSession(false);
    if (session == null) return;
    session.removeAttribute("SPRING_SECURITY_LAST_EXCEPTION");
  }
}
