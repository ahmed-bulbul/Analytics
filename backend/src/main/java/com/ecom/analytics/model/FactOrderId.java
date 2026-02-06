package com.ecom.analytics.model;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class FactOrderId implements Serializable {
  private Long shopId;
  private String orderGid;

  public FactOrderId() {}

  public FactOrderId(Long shopId, String orderGid) {
    this.shopId = shopId;
    this.orderGid = orderGid;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public String getOrderGid() {
    return orderGid;
  }

  public void setOrderGid(String orderGid) {
    this.orderGid = orderGid;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof FactOrderId that)) return false;
    return Objects.equals(shopId, that.shopId) && Objects.equals(orderGid, that.orderGid);
  }

  @Override
  public int hashCode() {
    return Objects.hash(shopId, orderGid);
  }
}
