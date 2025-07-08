package com.heybit.backend.security.config;

import com.heybit.backend.security.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class JwtSecurityConfig {

  public void configure(HttpSecurity http) throws Exception {
    JwtAuthenticationFilter filter = new JwtAuthenticationFilter();
    http.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);
  }
}