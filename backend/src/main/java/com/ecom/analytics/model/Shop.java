package com.ecom.analytics.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "shops")
public class Shop {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "shop_id")
  private Long id;

  @Column(name = "shop_domain", nullable = false, unique = true)
  private String shopDomain;

  @Column(nullable = false)
  private String currency;

  @Column(nullable = false)
  private String timezone;

  @Column(name = "last_backfill_through")
  private Instant lastBackfillThrough;

  @Column(name = "last_incremental_sync_at")
  private Instant lastIncrementalSyncAt;

  @Column(name = "shopify_access_token_encrypted")
  private String shopifyAccessTokenEncrypted;

  @Column(name = "shopify_access_token_iv")
  private String shopifyAccessTokenIv;

  @Column(name = "shopify_scopes")
  private String shopifyScopes;

  @Column(name = "shopify_installed_at")
  private Instant shopifyInstalledAt;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  @Column(name = "deleted_at")
  private Instant deletedAt;

  @Column(name = "deleted_by")
  private Long deletedBy;

  @Column(name = "platform_type", nullable = false)
  private String platformType;

  @Column(name = "platform_shop_id")
  private String platformShopId;

  @Column(name = "shop_name")
  private String shopName;

  @Column(name = "shop_owner_name")
  private String shopOwnerName;

  @Column(name = "shop_owner_email")
  private String shopOwnerEmail;

  @Column(name = "country_code", length = 2)
  private String countryCode;

  @Column(name = "shop_plan_name")
  private String shopPlanName;

  @Column(name = "is_active", nullable = false)
  private Boolean isActive;

  @PrePersist
  public void prePersist() {
    Instant now = Instant.now();
    createdAt = now;
    updatedAt = now;
    if (platformType == null) {
      platformType = "shopify";
    }
    if (isActive == null) {
      isActive = true;
    }
  }

  @PreUpdate
  public void preUpdate() {
    updatedAt = Instant.now();
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getShopDomain() {
    return shopDomain;
  }

  public void setShopDomain(String shopDomain) {
    this.shopDomain = shopDomain;
  }

  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }

  public String getTimezone() {
    return timezone;
  }

  public void setTimezone(String timezone) {
    this.timezone = timezone;
  }

  public Instant getLastBackfillThrough() {
    return lastBackfillThrough;
  }

  public void setLastBackfillThrough(Instant lastBackfillThrough) {
    this.lastBackfillThrough = lastBackfillThrough;
  }

  public Instant getLastIncrementalSyncAt() {
    return lastIncrementalSyncAt;
  }

  public void setLastIncrementalSyncAt(Instant lastIncrementalSyncAt) {
    this.lastIncrementalSyncAt = lastIncrementalSyncAt;
  }

  public String getShopifyAccessTokenEncrypted() {
    return shopifyAccessTokenEncrypted;
  }

  public void setShopifyAccessTokenEncrypted(String shopifyAccessTokenEncrypted) {
    this.shopifyAccessTokenEncrypted = shopifyAccessTokenEncrypted;
  }

  public String getShopifyAccessTokenIv() {
    return shopifyAccessTokenIv;
  }

  public void setShopifyAccessTokenIv(String shopifyAccessTokenIv) {
    this.shopifyAccessTokenIv = shopifyAccessTokenIv;
  }

  public String getShopifyScopes() {
    return shopifyScopes;
  }

  public void setShopifyScopes(String shopifyScopes) {
    this.shopifyScopes = shopifyScopes;
  }

  public Instant getShopifyInstalledAt() {
    return shopifyInstalledAt;
  }

  public void setShopifyInstalledAt(Instant shopifyInstalledAt) {
    this.shopifyInstalledAt = shopifyInstalledAt;
  }

  public Instant getDeletedAt() {
    return deletedAt;
  }

  public void setDeletedAt(Instant deletedAt) {
    this.deletedAt = deletedAt;
  }

  public Long getDeletedBy() {
    return deletedBy;
  }

  public void setDeletedBy(Long deletedBy) {
    this.deletedBy = deletedBy;
  }

  public String getPlatformType() {
    return platformType;
  }

  public void setPlatformType(String platformType) {
    this.platformType = platformType;
  }

  public String getPlatformShopId() {
    return platformShopId;
  }

  public void setPlatformShopId(String platformShopId) {
    this.platformShopId = platformShopId;
  }

  public String getShopName() {
    return shopName;
  }

  public void setShopName(String shopName) {
    this.shopName = shopName;
  }

  public String getShopOwnerName() {
    return shopOwnerName;
  }

  public void setShopOwnerName(String shopOwnerName) {
    this.shopOwnerName = shopOwnerName;
  }

  public String getShopOwnerEmail() {
    return shopOwnerEmail;
  }

  public void setShopOwnerEmail(String shopOwnerEmail) {
    this.shopOwnerEmail = shopOwnerEmail;
  }

  public String getCountryCode() {
    return countryCode;
  }

  public void setCountryCode(String countryCode) {
    this.countryCode = countryCode;
  }

  public String getShopPlanName() {
    return shopPlanName;
  }

  public void setShopPlanName(String shopPlanName) {
    this.shopPlanName = shopPlanName;
  }

  public Boolean getIsActive() {
    return isActive;
  }

  public void setIsActive(Boolean isActive) {
    this.isActive = isActive;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }
}
