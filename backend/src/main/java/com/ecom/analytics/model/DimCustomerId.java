package com.ecom.analytics.model;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class DimCustomerId implements Serializable {
  private Long shopId;
  private String customerGid;

  public DimCustomerId() {}

  public DimCustomerId(Long shopId, String customerGid) {
    this.shopId = shopId;
    this.customerGid = customerGid;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public String getCustomerGid() {
    return customerGid;
  }

  public void setCustomerGid(String customerGid) {
    this.customerGid = customerGid;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof DimCustomerId that)) return false;
    return Objects.equals(shopId, that.shopId) && Objects.equals(customerGid, that.customerGid);
  }

  @Override
  public int hashCode() {
    return Objects.hash(shopId, customerGid);
  }
}
