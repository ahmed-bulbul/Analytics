package com.ecom.analytics.model;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

@Embeddable
public class AggLtvCohortMonthId implements Serializable {
  private Long shopId;
  private LocalDate cohortMonth;
  private Integer monthsSinceFirstOrder;

  public AggLtvCohortMonthId() {}

  public AggLtvCohortMonthId(Long shopId, LocalDate cohortMonth, Integer monthsSinceFirstOrder) {
    this.shopId = shopId;
    this.cohortMonth = cohortMonth;
    this.monthsSinceFirstOrder = monthsSinceFirstOrder;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public LocalDate getCohortMonth() {
    return cohortMonth;
  }

  public void setCohortMonth(LocalDate cohortMonth) {
    this.cohortMonth = cohortMonth;
  }

  public Integer getMonthsSinceFirstOrder() {
    return monthsSinceFirstOrder;
  }

  public void setMonthsSinceFirstOrder(Integer monthsSinceFirstOrder) {
    this.monthsSinceFirstOrder = monthsSinceFirstOrder;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof AggLtvCohortMonthId that)) return false;
    return Objects.equals(shopId, that.shopId)
        && Objects.equals(cohortMonth, that.cohortMonth)
        && Objects.equals(monthsSinceFirstOrder, that.monthsSinceFirstOrder);
  }

  @Override
  public int hashCode() {
    return Objects.hash(shopId, cohortMonth, monthsSinceFirstOrder);
  }
}
