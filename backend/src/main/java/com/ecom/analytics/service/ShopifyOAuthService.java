package com.ecom.analytics.service;

import com.ecom.analytics.config.ShopifyConfig;
import com.ecom.analytics.model.Shop;
import com.ecom.analytics.model.ShopOAuthState;
import com.ecom.analytics.repository.ShopOAuthStateRepository;
import com.ecom.analytics.repository.ShopRepository;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class ShopifyOAuthService {
  private final ShopifyConfig config;
  private final ShopRepository shopRepository;
  private final ShopOAuthStateRepository oauthStateRepository;
  private final CryptoService cryptoService;
  private final AuditService auditService;
  private final RestClient restClient;

  public ShopifyOAuthService(ShopifyConfig config,
                             ShopRepository shopRepository,
                             ShopOAuthStateRepository oauthStateRepository,
                             CryptoService cryptoService,
                             AuditService auditService) {
    this.config = config;
    this.shopRepository = shopRepository;
    this.oauthStateRepository = oauthStateRepository;
    this.cryptoService = cryptoService;
    this.auditService = auditService;
    this.restClient = RestClient.builder()
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .build();
  }

  public String buildAuthUrl(long shopId, String clientId, String clientSecret, String shopDomain) {
    if (clientId == null || clientId.isBlank()) {
      throw new IllegalStateException("Shopify OAuth client ID is not configured. Set SHOPIFY_CLIENT_ID in backend/.env");
    }
    if (clientSecret == null || clientSecret.isBlank()) {
      throw new IllegalStateException("Shopify OAuth client secret is not configured. Set SHOPIFY_CLIENT_SECRET in backend/.env");
    }
    String state = UUID.randomUUID().toString();
    oauthStateRepository.save(new ShopOAuthState(shopId, state, Instant.now()));

    String scopes = URLEncoder.encode(config.getOauthScopes(), StandardCharsets.UTF_8);
    String redirect = URLEncoder.encode(config.getOauthRedirectUri(), StandardCharsets.UTF_8);

    return "https://" + shopDomain + "/admin/oauth/authorize" +
        "?client_id=" + clientId +
        "&scope=" + scopes +
        "&redirect_uri=" + redirect +
        "&state=" + state;
  }

  public void handleCallback(long shopId, String clientId, String clientSecret, String code, String state) {
    String expected = oauthStateRepository.findById(shopId)
        .map(ShopOAuthState::getState)
        .orElse(null);
    if (expected == null || !expected.equals(state)) {
      throw new IllegalArgumentException("Invalid OAuth state");
    }

    Shop shop = shopRepository.findByIdAndDeletedAtIsNull(shopId)
        .orElseThrow(() -> new IllegalStateException("Shop not found"));

    String url = "https://" + shop.getShopDomain() + "/admin/oauth/access_token";
    Map<String, Object> payload = Map.of(
        "client_id", clientId,
        "client_secret", clientSecret,
        "code", code
    );

    @SuppressWarnings("unchecked")
    Map<String, Object> response = restClient.post()
        .uri(url)
        .body(payload)
        .retrieve()
        .body(Map.class);

    if (response == null || response.get("access_token") == null) {
      throw new IllegalStateException("Failed to retrieve Shopify access token");
    }

    String token = response.get("access_token").toString();
    CryptoService.EncryptedPayload encrypted = cryptoService.encrypt(token);

    shop.setShopifyAccessTokenEncrypted(encrypted.cipherTextBase64());
    shop.setShopifyAccessTokenIv(encrypted.ivBase64());
    shop.setShopifyScopes(config.getOauthScopes());
    shop.setShopifyInstalledAt(Instant.now());
    shopRepository.save(shop);

    oauthStateRepository.deleteById(shopId);
    auditService.record("SHOPIFY_OAUTH_CONNECTED", null, shopId, java.util.Map.of("shopDomain", shop.getShopDomain()));
  }

  public long resolveShopIdByState(String state) {
    return oauthStateRepository.findByState(state)
        .map(ShopOAuthState::getShopId)
        .orElseThrow(() -> new IllegalArgumentException("Invalid OAuth state"));
  }
}
