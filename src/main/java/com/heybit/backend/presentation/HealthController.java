package com.heybit.backend.presentation;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

  @GetMapping("/")
  public String home() {
    return "Welcome!";
  }

  @RequestMapping(value = "/health", method = {RequestMethod.GET, RequestMethod.HEAD})
  public ResponseEntity<String> health() {
    return ResponseEntity.ok("OK");
  }

}