package com.ecom.analytics.controller;

import com.ecom.analytics.dto.LoginRequest;
import com.ecom.analytics.dto.LoginResponse;
import com.ecom.analytics.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @PostMapping("/login")
  public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
    return ResponseEntity.ok(authService.login(request));
  }

  @PostMapping("/register")
  public ResponseEntity<com.ecom.analytics.dto.RegisterResponse> register(@RequestBody com.ecom.analytics.dto.RegisterRequest request) {
    return ResponseEntity.ok(authService.register(request));
  }
}
