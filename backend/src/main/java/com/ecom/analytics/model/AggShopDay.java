package com.ecom.analytics.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "agg_shop_day")
public class AggShopDay {
  @EmbeddedId
  private AggShopDayId id;

  @Column(name = "orders_count", nullable = false)
  private Integer ordersCount;

  @Column(name = "revenue_gross", nullable = false)
  private BigDecimal revenueGross;

  @Column(name = "net_sales", nullable = false)
  private BigDecimal netSales;

  @Column(name = "tax_total", nullable = false)
  private BigDecimal taxTotal;

  @Column(name = "shipping_total", nullable = false)
  private BigDecimal shippingTotal;

  @Column(name = "new_orders_count", nullable = false)
  private Integer newOrdersCount;

  @Column(name = "returning_orders_count", nullable = false)
  private Integer returningOrdersCount;

  @Column(name = "new_revenue_gross", nullable = false)
  private BigDecimal newRevenueGross;

  @Column(name = "returning_revenue_gross", nullable = false)
  private BigDecimal returningRevenueGross;

  public AggShopDayId getId() {
    return id;
  }

  public void setId(AggShopDayId id) {
    this.id = id;
  }

  public Integer getOrdersCount() {
    return ordersCount;
  }

  public void setOrdersCount(Integer ordersCount) {
    this.ordersCount = ordersCount;
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

  public BigDecimal getTaxTotal() {
    return taxTotal;
  }

  public void setTaxTotal(BigDecimal taxTotal) {
    this.taxTotal = taxTotal;
  }

  public BigDecimal getShippingTotal() {
    return shippingTotal;
  }

  public void setShippingTotal(BigDecimal shippingTotal) {
    this.shippingTotal = shippingTotal;
  }

  public Integer getNewOrdersCount() {
    return newOrdersCount;
  }

  public void setNewOrdersCount(Integer newOrdersCount) {
    this.newOrdersCount = newOrdersCount;
  }

  public Integer getReturningOrdersCount() {
    return returningOrdersCount;
  }

  public void setReturningOrdersCount(Integer returningOrdersCount) {
    this.returningOrdersCount = returningOrdersCount;
  }

  public BigDecimal getNewRevenueGross() {
    return newRevenueGross;
  }

  public void setNewRevenueGross(BigDecimal newRevenueGross) {
    this.newRevenueGross = newRevenueGross;
  }

  public BigDecimal getReturningRevenueGross() {
    return returningRevenueGross;
  }

  public void setReturningRevenueGross(BigDecimal returningRevenueGross) {
    this.returningRevenueGross = returningRevenueGross;
  }
}
