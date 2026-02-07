package com.ecom.analytics.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "shop_platform_credentials",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"shop_id", "platform_type"})
    }
)
public class ShopPlatformCredentials {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "credential_id")
  private Long id;

  @Column(name = "shop_id", nullable = false)
  private Long shopId;

  @Column(name = "platform_type", nullable = false, length = 20)
  private String platformType;

  @Column(name = "access_token_encrypted")
  private String accessTokenEncrypted;

  @Column(name = "access_token_iv")
  private String accessTokenIv;

  @Column(name = "refresh_token_encrypted")
  private String refreshTokenEncrypted;

  @Column(name = "refresh_token_iv")
  private String refreshTokenIv;

  @Column(name = "api_key_encrypted")
  private String apiKeyEncrypted;

  @Column(name = "api_secret_encrypted")
  private String apiSecretEncrypted;

  @Column(name = "scopes")
  private String scopes;

  @Column(name = "credentials_metadata", columnDefinition = "jsonb")
  private String credentialsMetadata;

  @Column(name = "installed_at")
  private Instant installedAt;

  @Column(name = "expires_at")
  private Instant expiresAt;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  @PrePersist
  public void prePersist() {
    Instant now = Instant.now();
    createdAt = now;
    updatedAt = now;
  }

  @PreUpdate
  public void preUpdate() {
    updatedAt = Instant.now();
  }

  // Getters and Setters
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public String getPlatformType() {
    return platformType;
  }

  public void setPlatformType(String platformType) {
    this.platformType = platformType;
  }

  public String getAccessTokenEncrypted() {
    return accessTokenEncrypted;
  }

  public void setAccessTokenEncrypted(String accessTokenEncrypted) {
    this.accessTokenEncrypted = accessTokenEncrypted;
  }

  public String getAccessTokenIv() {
    return accessTokenIv;
  }

  public void setAccessTokenIv(String accessTokenIv) {
    this.accessTokenIv = accessTokenIv;
  }

  public String getRefreshTokenEncrypted() {
    return refreshTokenEncrypted;
  }

  public void setRefreshTokenEncrypted(String refreshTokenEncrypted) {
    this.refreshTokenEncrypted = refreshTokenEncrypted;
  }

  public String getRefreshTokenIv() {
    return refreshTokenIv;
  }

  public void setRefreshTokenIv(String refreshTokenIv) {
    this.refreshTokenIv = refreshTokenIv;
  }

  public String getApiKeyEncrypted() {
    return apiKeyEncrypted;
  }

  public void setApiKeyEncrypted(String apiKeyEncrypted) {
    this.apiKeyEncrypted = apiKeyEncrypted;
  }

  public String getApiSecretEncrypted() {
    return apiSecretEncrypted;
  }

  public void setApiSecretEncrypted(String apiSecretEncrypted) {
    this.apiSecretEncrypted = apiSecretEncrypted;
  }

  public String getScopes() {
    return scopes;
  }

  public void setScopes(String scopes) {
    this.scopes = scopes;
  }

  public String getCredentialsMetadata() {
    return credentialsMetadata;
  }

  public void setCredentialsMetadata(String credentialsMetadata) {
    this.credentialsMetadata = credentialsMetadata;
  }

  public Instant getInstalledAt() {
    return installedAt;
  }

  public void setInstalledAt(Instant installedAt) {
    this.installedAt = installedAt;
  }

  public Instant getExpiresAt() {
    return expiresAt;
  }

  public void setExpiresAt(Instant expiresAt) {
    this.expiresAt = expiresAt;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }
}
