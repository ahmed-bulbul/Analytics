package com.ecom.analytics.service;

import com.ecom.analytics.dto.OnboardRequest;
import com.ecom.analytics.dto.OnboardResponse;
import com.ecom.analytics.repository.ShopRepository;
import com.ecom.analytics.repository.UserRepository;
import com.ecom.analytics.repository.UserShopRepository;
import com.ecom.analytics.repository.UserWriteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ShopOnboardingService {
  private final ShopRepository shopRepository;
  private final UserWriteRepository userWriteRepository;
  private final UserShopRepository userShopRepository;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final ShopifyOAuthService oauthService;

  public ShopOnboardingService(
      ShopRepository shopRepository,
      UserWriteRepository userWriteRepository,
      UserShopRepository userShopRepository,
      UserRepository userRepository,
      PasswordEncoder passwordEncoder,
      ShopifyOAuthService oauthService
  ) {
    this.shopRepository = shopRepository;
    this.userWriteRepository = userWriteRepository;
    this.userShopRepository = userShopRepository;
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.oauthService = oauthService;
  }

  public OnboardResponse onboard(OnboardRequest request) {
    if (request.shopDomain() == null || request.shopDomain().isBlank()) {
      throw new IllegalArgumentException("shopDomain is required");
    }
    if (request.adminEmail() == null || request.adminEmail().isBlank()) {
      throw new IllegalArgumentException("adminEmail is required");
    }
    if (request.adminPassword() == null || request.adminPassword().isBlank()) {
      throw new IllegalArgumentException("adminPassword is required");
    }
    if (shopRepository.shopDomainExists(request.shopDomain().toLowerCase())) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Shop domain already exists. Use a different domain.");
    }
    if (userRepository.findByEmail(request.adminEmail().toLowerCase()).isPresent()) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Admin email already exists. Use a different email.");
    }

    long shopId = shopRepository.createShop(request.shopDomain().toLowerCase());
    String hash = passwordEncoder.encode(request.adminPassword());
    long userId = userWriteRepository.insertUser(request.adminEmail().toLowerCase(), hash, shopId, "ADMIN");
    userShopRepository.addUserToShop(userId, shopId);

    String oauthUrl = oauthService.buildAuthUrl(shopId, request.shopDomain().toLowerCase());
    return new OnboardResponse(shopId, request.shopDomain().toLowerCase(), oauthUrl);
  }

  public void handleCallback(long shopId, String code, String state) {
    oauthService.handleCallback(shopId, code, state);
  }

  public void handleCallbackByState(String code, String state) {
    Long shopId = oauthService.resolveShopIdByState(state);
    oauthService.handleCallback(shopId, code, state);
  }
}
