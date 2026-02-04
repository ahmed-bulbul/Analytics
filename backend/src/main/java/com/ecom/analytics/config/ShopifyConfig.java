package com.ecom.analytics.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "shopify")
public class ShopifyConfig {
  private String baseUrl;
  private String accessToken;
  private String apiVersion;
  private String oauthClientId;
  private String oauthClientSecret;
  private String oauthRedirectUri;
  private String oauthScopes;

  public String getBaseUrl() {
    return baseUrl;
  }

  public void setBaseUrl(String baseUrl) {
    this.baseUrl = baseUrl;
  }

  public String getAccessToken() {
    return accessToken;
  }

  public void setAccessToken(String accessToken) {
    this.accessToken = accessToken;
  }

  public String getApiVersion() {
    return apiVersion;
  }

  public void setApiVersion(String apiVersion) {
    this.apiVersion = apiVersion;
  }

  public String getOauthClientId() {
    return oauthClientId;
  }

  public void setOauthClientId(String oauthClientId) {
    this.oauthClientId = oauthClientId;
  }

  public String getOauthClientSecret() {
    return oauthClientSecret;
  }

  public void setOauthClientSecret(String oauthClientSecret) {
    this.oauthClientSecret = oauthClientSecret;
  }

  public String getOauthRedirectUri() {
    return oauthRedirectUri;
  }

  public void setOauthRedirectUri(String oauthRedirectUri) {
    this.oauthRedirectUri = oauthRedirectUri;
  }

  public String getOauthScopes() {
    return oauthScopes;
  }

  public void setOauthScopes(String oauthScopes) {
    this.oauthScopes = oauthScopes;
  }
}
