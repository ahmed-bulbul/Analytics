package com.ecom.analytics.model;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class UserShopId implements Serializable {
  private Long userId;
  private Long shopId;

  public UserShopId() {}

  public UserShopId(Long userId, Long shopId) {
    this.userId = userId;
    this.shopId = shopId;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof UserShopId that)) return false;
    return Objects.equals(userId, that.userId) && Objects.equals(shopId, that.shopId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(userId, shopId);
  }
}
