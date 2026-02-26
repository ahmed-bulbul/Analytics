package com.ecom.analytics.service;

import com.ecom.analytics.dto.OnboardRequest;
import com.ecom.analytics.dto.OnboardResponse;
import com.ecom.analytics.model.Shop;
import com.ecom.analytics.model.User;
import com.ecom.analytics.model.UserShop;
import com.ecom.analytics.repository.ShopRepository;
import com.ecom.analytics.repository.UserRepository;
import com.ecom.analytics.repository.UserShopRepository;
import com.ecom.analytics.security.SecurityContextProvider;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ShopOnboardingService {
  private final ShopRepository shopRepository;
  private final UserRepository userRepository;
  private final UserShopRepository userShopRepository;
  private final PasswordEncoder passwordEncoder;
  private final ShopifyOAuthService oauthService;
  private final AuditService auditService;
  private final SecurityContextProvider provider;

  public ShopOnboardingService(
      ShopRepository shopRepository,
      UserRepository userRepository,
      UserShopRepository userShopRepository,
      PasswordEncoder passwordEncoder,
      ShopifyOAuthService oauthService,
      AuditService auditService
  ) {
    this.shopRepository = shopRepository;
    this.userRepository = userRepository;
    this.userShopRepository = userShopRepository;
    this.passwordEncoder = passwordEncoder;
    this.oauthService = oauthService;
    this.auditService = auditService;
  }

  public OnboardResponse onboard(OnboardRequest request) {
    if (request.shopDomain() == null || request.shopDomain().isBlank()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "shopDomain is required");
    }
    if (request.adminEmail() == null || request.adminEmail().isBlank()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "adminEmail is required");
    }
    if (request.adminPassword() == null || request.adminPassword().isBlank()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "adminPassword is required");
    }

    String shopDomain = request.shopDomain().toLowerCase();
    if (shopRepository.existsByShopDomainIgnoreCaseAndDeletedAtIsNull(shopDomain)) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Shop domain already exists. Use a different domain.");
    }
    if (userRepository.findByEmailIgnoreCaseAndDeletedAtIsNull(request.adminEmail()).isPresent()) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Admin email already exists. Use a different email.");
    }

    Shop shop = new Shop();
    shop.setShopDomain(shopDomain);
    shop.setCurrency("USD");
    shop.setTimezone("UTC");
    shop = shopRepository.save(shop);

    User user = new User();
    user.setEmail(request.adminEmail().toLowerCase());
    user.setPasswordHash(passwordEncoder.encode(request.adminPassword()));
    user.setPrimaryShop(shop);
    user.setRole("ADMIN");
    user.setClientId(request.clientId());
    user.setClientSecret(request.clientId());
    user = userRepository.save(user);

    userShopRepository.save(new UserShop(user, shop));

    auditService.recordAs("SHOP_ONBOARDED",
        user.getId(),
        user.getEmail(),
        user.getId(),
        shop.getId(),
        java.util.Map.of("shopDomain", shopDomain));

    String oauthUrl = oauthService.buildAuthUrl(shop.getId(), user.getClientId(), user.getClientSecret, user.getClientshopDomain);
    return new OnboardResponse(shop.getId(), shopDomain, oauthUrl);
  }

  public void handleCallbackByState(String code, String clientSecret) {
    Long shopId = oauthService.resolveShopIdByState(state);
    User user = provider.<User>getUser().orElseGet(()->{throw new Exception("User is required");});
    oauthService.handleCallback(shopId, clientId, clientSecret, code, state);
    auditService.record("SHOP_OAUTH_CONNECTED", null, shopId, java.util.Map.of("state", state));
  }
}
