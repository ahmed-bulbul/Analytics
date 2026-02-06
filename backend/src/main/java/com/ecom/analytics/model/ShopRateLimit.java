package com.ecom.analytics.model;

import jakarta.persistence.*;

@Entity
@Table(name = "shop_rate_limit")
public class ShopRateLimit {
  @Id
  @Column(name = "shop_id")
  private Long shopId;

  @Column(name = "enabled", nullable = false)
  private boolean enabled = true;

  @Column(name = "capacity", nullable = false)
  private int capacity;

  @Column(name = "refill_per_minute", nullable = false)
  private int refillPerMinute;

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public int getCapacity() {
    return capacity;
  }

  public void setCapacity(int capacity) {
    this.capacity = capacity;
  }

  public int getRefillPerMinute() {
    return refillPerMinute;
  }

  public void setRefillPerMinute(int refillPerMinute) {
    this.refillPerMinute = refillPerMinute;
  }
}
