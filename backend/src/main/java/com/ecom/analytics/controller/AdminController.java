package com.ecom.analytics.controller;

import com.ecom.analytics.dto.RateLimitOverrideRequest;
import com.ecom.analytics.security.AuthPrincipal;
import com.ecom.analytics.service.AdminSoftDeleteService;
import com.ecom.analytics.service.RateLimitOverrideService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
  private final AdminSoftDeleteService adminSoftDeleteService;
  private final RateLimitOverrideService rateLimitOverrideService;

  public AdminController(AdminSoftDeleteService adminSoftDeleteService, RateLimitOverrideService rateLimitOverrideService) {
    this.adminSoftDeleteService = adminSoftDeleteService;
    this.rateLimitOverrideService = rateLimitOverrideService;
  }

  @DeleteMapping("/users/{userId}")
  public ResponseEntity<Void> deleteUser(@PathVariable long userId, @AuthenticationPrincipal AuthPrincipal principal) {
    if (principal == null) {
      return ResponseEntity.status(401).build();
    }
    adminSoftDeleteService.softDeleteUser(userId, principal.userId());
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/shops/{shopId}")
  public ResponseEntity<Void> deleteShop(@PathVariable long shopId, @AuthenticationPrincipal AuthPrincipal principal) {
    if (principal == null) {
      return ResponseEntity.status(401).build();
    }
    adminSoftDeleteService.softDeleteShop(shopId, principal.userId());
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/shops/{shopId}/rate-limit")
  public ResponseEntity<Void> updateRateLimit(
      @PathVariable long shopId,
      @RequestBody RateLimitOverrideRequest request,
      @AuthenticationPrincipal AuthPrincipal principal
  ) {
    if (principal == null) {
      return ResponseEntity.status(401).build();
    }
    rateLimitOverrideService.upsert(shopId, request);
    return ResponseEntity.noContent().build();
  }
}
