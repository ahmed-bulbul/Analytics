package com.ecom.analytics.model;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

@Embeddable
public class AggShopDayWithSpendId implements Serializable {
  private Long shopId;
  private LocalDate date;

  public AggShopDayWithSpendId() {}

  public AggShopDayWithSpendId(Long shopId, LocalDate date) {
    this.shopId = shopId;
    this.date = date;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public LocalDate getDate() {
    return date;
  }

  public void setDate(LocalDate date) {
    this.date = date;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof AggShopDayWithSpendId that)) return false;
    return Objects.equals(shopId, that.shopId) && Objects.equals(date, that.date);
  }

  @Override
  public int hashCode() {
    return Objects.hash(shopId, date);
  }
}
