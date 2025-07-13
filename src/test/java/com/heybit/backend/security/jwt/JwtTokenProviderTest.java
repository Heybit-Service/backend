package com.heybit.backend.security.jwt;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.heybit.backend.domain.user.Role;
import com.heybit.backend.security.oauth.CustomOAuth2User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

@SpringBootTest(properties = "jwt.secret=test-secret-key-123456789012345678901234567890")
class JwtTokenProviderTest {

  @Autowired
  private JwtTokenProvider jwtTokenProvider;

  private CustomOAuth2User userPrincipal;

  private Key testKey;

  @BeforeEach
  void setUp() {
    testKey = Keys.hmacShaKeyFor(
        "test-secret-key-123456789012345678901234567890".getBytes(StandardCharsets.UTF_8));

    userPrincipal = new CustomOAuth2User(
        Collections.emptyList(),
        Map.of("email", "jun@example.com"),
        "email",
        1L,
        "jun",
        "jun@example.com",
        Role.USER
    );
  }

  @DisplayName("Access Token 생성 성공")
  @Test
  void createAccessToken() {
    String token = jwtTokenProvider.createAccessToken(userPrincipal);
    assertThat(token).isNotNull();
  }

  @DisplayName("Authentication 객체를 반환")
  @Test
  void getAuthentication() {
    String token = jwtTokenProvider.createAccessToken(userPrincipal);
    Authentication authentication = jwtTokenProvider.getAuthentication(token);

    assertThat(authentication).isInstanceOf(UsernamePasswordAuthenticationToken.class);
    assertThat(authentication.getPrincipal()).isNotNull();
  }

  @DisplayName("토큰에서 클레임을 추출")
  @Test
  void extractClaims() {
    String token = jwtTokenProvider.createAccessToken(userPrincipal);
    Claims claims = JwtUtils.extractClaims(token, testKey);

    assertThat(claims.get("name")).isEqualTo(userPrincipal.getNickname());
    assertThat(claims.get("sub", Integer.class)).isEqualTo(userPrincipal.getId().intValue());
    assertThat(claims.get("role")).isEqualTo(userPrincipal.getRole().name());
  }

  @DisplayName("토큰 유효성 검사 - 성공 테스트")
  @Test
  void validateToken_success() {
    String token = jwtTokenProvider.createAccessToken(userPrincipal);
    boolean result = jwtTokenProvider.validateToken(token);
    assertThat(result).isTrue();
  }


  @DisplayName("토큰 유효성 검사 - 실패 테스트 만료된 토큰")
  @Test
  void validateToken_fail_expired() {
    Map<String, Object> claims = new HashMap<>();
    claims.put("sub", userPrincipal.getEmail());
    claims.put("email", userPrincipal.getEmail());

    long now = System.currentTimeMillis();
    String expiredToken = Jwts.builder()
        .setClaims(claims)
        .setIssuedAt(new Date(now - 2000))
        .setExpiration(new Date(now - 1000))
        .signWith(testKey, SignatureAlgorithm.HS256)
        .compact();

    boolean result = jwtTokenProvider.validateToken(expiredToken);
    assertThat(result).isFalse();
  }

  @DisplayName("토큰 유효성 검사 - 실패 테스트 유효하지않은 서명")
  @Test
  void validateToken_fail_invalidSignature() {
    Map<String, Object> claims = new HashMap<>();
    claims.put("sub", userPrincipal.getEmail());
    claims.put("email", userPrincipal.getEmail());

    Key fakeKey = Keys.hmacShaKeyFor(
        "fake-secret-key-123456789012345678901234567890".getBytes(StandardCharsets.UTF_8));

    String fakeKToken = Jwts.builder()
        .setClaims(claims)
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + 100000))
        .signWith(fakeKey, SignatureAlgorithm.HS256)
        .compact();

    boolean result = jwtTokenProvider.validateToken(fakeKToken);
    assertThat(result).isFalse();
    ;
  }

  @DisplayName("토큰 유효성 검사 - 실패 테스트 빈 토큰")
  @Test
  void validateToken__fail_empty() {
    boolean result = jwtTokenProvider.validateToken("");
    assertThat(result).isFalse();
  }

  @Test
  void validateToken_fail_null() {
    boolean result = jwtTokenProvider.validateToken(null);
    assertThat(result).isFalse();
  }


}