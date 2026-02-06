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
}
