package com.heybit.backend.test;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JwtTestController {

  @GetMapping("/api/dev/auth-check")
  public String checkJwt(Authentication authentication) {
    if (authentication == null || !authentication.isAuthenticated()) {
      return "Unauthenticated";
    }
    return "Authenticated";
  }
}
