package com.ecom.analytics.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "shop_oauth_state")
public class ShopOAuthState {
  @Id
  @Column(name = "shop_id")
  private Long shopId;

  @Column(nullable = false)
  private String state;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  public ShopOAuthState() {}

  public ShopOAuthState(Long shopId, String state, Instant createdAt) {
    this.shopId = shopId;
    this.state = state;
    this.createdAt = createdAt;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
  }
}
