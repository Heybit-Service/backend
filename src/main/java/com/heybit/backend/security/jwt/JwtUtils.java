package com.heybit.backend.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import java.security.Key;
import org.springframework.stereotype.Component;

@Component
public class JwtUtils {
  public static Claims extractClaims(String token, Key signingKey) {
    try {
      Jws<Claims> parsed = Jwts.parserBuilder()
          .setSigningKey(signingKey)
          .build()
          .parseClaimsJws(token);
      return parsed.getBody();
    } catch (JwtException e) {
      throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
    }
  }

  public static String extractSubject(String token, Key key) {
    return extractClaims(token, key).getSubject();
  }

  public static long getRemainingTime(String token, Key signingKey) {
    Claims claims = extractClaims(token, signingKey);
    long now = System.currentTimeMillis();
    return claims.getExpiration().getTime() - now;
  }
}
