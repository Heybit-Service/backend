package com.heybit.backend.security.jwt;

import com.heybit.backend.security.oauth.CustomOAuth2User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class JwtTokenProvider {

  private final Key key;
  private static final long ACCESS_TOKEN_EXPIRATION = 1000L * 60 * 60; // 60분


  public JwtTokenProvider(@Value("${jwt.secret}") String secretKey) {
    this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
  }


  public String createAccessToken(CustomOAuth2User userPrincipal) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("sub", userPrincipal.getId());
    claims.put("name", userPrincipal.getNickname());
    claims.put("role", userPrincipal.getRole().name());
    return createAccessToken(claims);
  }

  private String createAccessToken(Map<String, Object> claims) {
    long now = System.currentTimeMillis();
    return Jwts.builder()
        .setClaims(claims)
        .setIssuedAt(new Date(now))
        .setExpiration(new Date(now + ACCESS_TOKEN_EXPIRATION))
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();
  }

  public Authentication getAuthentication(String token) {
    Claims claims = JwtUtils.extractClaims(token, key);
    String userId = claims.getSubject();
    String role = (String) claims.get("role");

    User principal = new User(userId, "", Collections.singleton(new SimpleGrantedAuthority("ROLE_" + role)));
    return new UsernamePasswordAuthenticationToken(principal, token, principal.getAuthorities());
  }

  public String resolveToken(HttpServletRequest request) {
    String bearerToken = request.getHeader("Authorization");
    if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
      return bearerToken.substring(7);
    }
    return null;
  }

  public boolean validateToken(String token) {
    try {
      JwtUtils.extractClaims(token, key);
      return true;
    } catch (ExpiredJwtException e) {
      log.warn("JWT token expired: {}", e.getMessage());
    } catch (MalformedJwtException e) {
      log.warn("Malformed JWT token: {}", e.getMessage());
    } catch (SignatureException e) {
      log.warn("Invalid JWT signature: {}", e.getMessage());
    } catch (Exception e) {
      log.error("Unknown JWT validation error: {}", e.getMessage());
    }
    return false;
  }

  public long getRemainingTime(String token) {
    return JwtUtils.getRemainingTime(token, key);
  }
}
