package com.ecom.analytics.controller;

import com.ecom.analytics.dto.OnboardRequest;
import com.ecom.analytics.dto.OnboardResponse;
import com.ecom.analytics.dto.GrantAccessRequest;
import com.ecom.analytics.dto.GrantAccessResponse;
import com.ecom.analytics.dto.RevokeAccessRequest;
import com.ecom.analytics.dto.RevokeAccessResponse;
import com.ecom.analytics.dto.ShopRow;
import com.ecom.analytics.dto.UserShopRow;
import com.ecom.analytics.service.ShopOnboardingService;
import com.ecom.analytics.service.AccessService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/shops")
public class ShopController {
  private final ShopOnboardingService onboardingService;
  private final AccessService accessService;

  public ShopController(ShopOnboardingService onboardingService, AccessService accessService) {
    this.onboardingService = onboardingService;
    this.accessService = accessService;
  }

  @PostMapping("/onboard")
  public ResponseEntity<OnboardResponse> onboard(@RequestBody OnboardRequest request) {
    return ResponseEntity.ok(onboardingService.onboard(request));
  }

  @GetMapping("/onboard/callback")
  public ResponseEntity<String> callback(
      @RequestParam String code,
      @RequestParam String state
  ) {
    onboardingService.handleCallbackByState(code, state);
    return ResponseEntity.ok("Shopify connected");
  }

  @PostMapping("/grant-access")
  public ResponseEntity<GrantAccessResponse> grantAccess(@RequestBody GrantAccessRequest request) {
    return ResponseEntity.ok(accessService.grantAccess(request));
  }

  @PostMapping("/revoke-access")
  public ResponseEntity<RevokeAccessResponse> revokeAccess(@RequestBody RevokeAccessRequest request) {
    return ResponseEntity.ok(accessService.revokeAccess(request));
  }

  @GetMapping("/users")
  public ResponseEntity<com.ecom.analytics.dto.PageResult<UserShopRow>> listUsers(
      @RequestParam long shopId,
      @RequestParam(required = false) String email,
      @RequestParam(defaultValue = "email") String sortBy,
      @RequestParam(defaultValue = "asc") String sortDir,
      @RequestParam(defaultValue = "50") int limit,
      @RequestParam(defaultValue = "0") int offset,
      @RequestParam(required = false) String cursor
  ) {
    String dir = normalizeSortDir(sortDir);
    String field = normalizeUserSortBy(sortBy);
    int normalizedLimit = normalizeLimit(limit);
    if (cursor != null) {
      return ResponseEntity.ok(accessService.listUsersForShopCursor(shopId, email, normalizedLimit, dir, cursor));
    }
    if (!field.equals("email")) {
      field = "email";
    }
    return ResponseEntity.ok(accessService.listUsersForShop(shopId, email, normalizedLimit, normalizeOffset(offset), dir));
  }

  @GetMapping("/shops")
  public ResponseEntity<com.ecom.analytics.dto.PageResult<ShopRow>> listShops(
      @RequestParam long userId,
      @RequestParam(required = false) String domain,
      @RequestParam(defaultValue = "domain") String sortBy,
      @RequestParam(defaultValue = "asc") String sortDir,
      @RequestParam(defaultValue = "50") int limit,
      @RequestParam(defaultValue = "0") int offset,
      @RequestParam(required = false) String cursor
  ) {
    String dir = normalizeSortDir(sortDir);
    String field = normalizeShopSortBy(sortBy);
    int normalizedLimit = normalizeLimit(limit);
    if (cursor != null) {
      return ResponseEntity.ok(accessService.listShopsForUserCursor(userId, domain, normalizedLimit, dir, cursor));
    }
    if (!field.equals("domain")) {
      field = "domain";
    }
    return ResponseEntity.ok(accessService.listShopsForUser(userId, domain, normalizedLimit, normalizeOffset(offset), dir));
  }

  private int normalizeLimit(int limit) {
    if (limit < 1) {
      return 50;
    }
    return Math.min(limit, 200);
  }

  private int normalizeOffset(int offset) {
    return Math.max(offset, 0);
  }

  private String normalizeSortDir(String sortDir) {
    if (sortDir == null) {
      return "ASC";
    }
    return sortDir.equalsIgnoreCase("desc") ? "DESC" : "ASC";
  }

  private String normalizeUserSortBy(String sortBy) {
    if (sortBy == null || sortBy.isBlank()) {
      return "email";
    }
    return "email";
  }

  private String normalizeShopSortBy(String sortBy) {
    if (sortBy == null || sortBy.isBlank()) {
      return "domain";
    }
    return "domain";
  }
}
