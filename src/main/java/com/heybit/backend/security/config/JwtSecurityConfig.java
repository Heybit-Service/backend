package com.heybit.backend.security.config;

import com.heybit.backend.security.jwt.JwtAuthenticationFilter;
import com.heybit.backend.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class JwtSecurityConfig {

  private final JwtTokenProvider jwtTokenProvider;

  public void configure(HttpSecurity http) throws Exception {
    JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtTokenProvider);
    http.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);
  }
}