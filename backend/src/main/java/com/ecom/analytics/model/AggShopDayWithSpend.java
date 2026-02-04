package com.ecom.analytics.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "agg_shop_day_with_spend")
public class AggShopDayWithSpend {
  @EmbeddedId
  private AggShopDayWithSpendId id;

  @Column(name = "revenue_gross", nullable = false)
  private BigDecimal revenueGross;

  @Column(name = "net_sales", nullable = false)
  private BigDecimal netSales;

  @Column(name = "orders_count", nullable = false)
  private Integer ordersCount;

  @Column(name = "ad_spend_total", nullable = false)
  private BigDecimal adSpendTotal;

  @Column(name = "mer", nullable = false)
  private BigDecimal mer;

  public AggShopDayWithSpendId getId() {
    return id;
  }

  public void setId(AggShopDayWithSpendId id) {
    this.id = id;
  }

  public BigDecimal getRevenueGross() {
    return revenueGross;
  }

  public void setRevenueGross(BigDecimal revenueGross) {
    this.revenueGross = revenueGross;
  }

  public BigDecimal getNetSales() {
    return netSales;
  }

  public void setNetSales(BigDecimal netSales) {
    this.netSales = netSales;
  }

  public Integer getOrdersCount() {
    return ordersCount;
  }

  public void setOrdersCount(Integer ordersCount) {
    this.ordersCount = ordersCount;
  }

  public BigDecimal getAdSpendTotal() {
    return adSpendTotal;
  }

  public void setAdSpendTotal(BigDecimal adSpendTotal) {
    this.adSpendTotal = adSpendTotal;
  }

  public BigDecimal getMer() {
    return mer;
  }

  public void setMer(BigDecimal mer) {
    this.mer = mer;
  }
}
