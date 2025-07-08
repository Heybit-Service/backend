package com.heybit.backend.security.config;

import com.heybit.backend.security.jwt.JwtAuthenticationFilter;
import com.heybit.backend.security.oauth.CustomOAuth2UserService;
import com.heybit.backend.security.oauth.OAuth2LoginFailureHandler;
import com.heybit.backend.security.oauth.OAuth2LoginSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final JwtSecurityConfig jwtSecurityConfig;
  private final OAuth2SecurityConfig oAuth2SecurityConfig;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable())
        .formLogin(form -> form.disable())
        .httpBasic(basic -> basic.disable())
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 완전 Stateless 구조

        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/", "/login/**", "/oauth2/**", "/h2-console/**").permitAll()
            .anyRequest().authenticated()
        );

    // JWT 필터 설정 위임
    jwtSecurityConfig.configure(http);

    // OAuth2 로그인 설정 위임
    oAuth2SecurityConfig.configure(http);

    return http.build();
  }
}
