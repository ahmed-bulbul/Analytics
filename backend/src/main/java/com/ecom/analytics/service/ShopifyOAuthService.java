package com.ecom.analytics.service;

import com.ecom.analytics.config.ShopifyConfig;
import com.ecom.analytics.repository.ShopRepository;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import com.ecom.analytics.repository.OAuthStateRepository;
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
  private final OAuthStateRepository oauthStateRepository;
  private final RestClient restClient;

  public ShopifyOAuthService(ShopifyConfig config, ShopRepository shopRepository, OAuthStateRepository oauthStateRepository) {
    this.config = config;
    this.shopRepository = shopRepository;
    this.oauthStateRepository = oauthStateRepository;
    this.restClient = RestClient.builder()
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .build();
  }

  public String buildAuthUrl(long shopId, String shopDomain) {
    if (config.getOauthClientId() == null || config.getOauthClientId().isBlank()) {
      throw new IllegalStateException("Shopify OAuth client ID is not configured. Set SHOPIFY_CLIENT_ID in backend/.env");
    }
    if (config.getOauthClientSecret() == null || config.getOauthClientSecret().isBlank()) {
      throw new IllegalStateException("Shopify OAuth client secret is not configured. Set SHOPIFY_CLIENT_SECRET in backend/.env");
    }
    String state = UUID.randomUUID().toString();
    oauthStateRepository.saveState(shopId, state);

    String scopes = URLEncoder.encode(config.getOauthScopes(), StandardCharsets.UTF_8);
    String redirect = URLEncoder.encode(config.getOauthRedirectUri(), StandardCharsets.UTF_8);

    return "https://" + shopDomain + "/admin/oauth/authorize" +
        "?client_id=" + config.getOauthClientId() +
        "&scope=" + scopes +
        "&redirect_uri=" + redirect +
        "&state=" + state;
  }

  public void handleCallback(long shopId, String code, String state) {
    String expected = oauthStateRepository.findState(shopId).orElse(null);
    if (expected == null || !expected.equals(state)) {
      throw new IllegalArgumentException("Invalid OAuth state");
    }

    String shopDomain = shopRepository.findShopDomain(shopId);
    String url = "https://" + shopDomain + "/admin/oauth/access_token";
    Map<String, Object> payload = Map.of(
        "client_id", config.getOauthClientId(),
        "client_secret", config.getOauthClientSecret(),
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
    shopRepository.storeShopifyToken(shopId, token, config.getOauthScopes());
    oauthStateRepository.deleteState(shopId);
  }

  public long resolveShopIdByState(String state) {
    return oauthStateRepository.findShopIdByState(state)
        .orElseThrow(() -> new IllegalArgumentException("Invalid OAuth state"));
  }
}
