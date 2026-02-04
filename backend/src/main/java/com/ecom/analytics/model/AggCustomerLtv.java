package com.ecom.analytics.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "agg_customer_ltv")
public class AggCustomerLtv {
  @EmbeddedId
  private AggCustomerLtvId id;

  @Column(name = "first_order_processed_at")
  private Instant firstOrderProcessedAt;

  @Column(name = "last_order_processed_at")
  private Instant lastOrderProcessedAt;

  @Column(name = "lifetime_orders_count", nullable = false)
  private Integer lifetimeOrdersCount;

  @Column(name = "lifetime_revenue_gross", nullable = false)
  private BigDecimal lifetimeRevenueGross;

  @Column(name = "lifetime_net_sales", nullable = false)
  private BigDecimal lifetimeNetSales;

  public AggCustomerLtvId getId() {
    return id;
  }

  public void setId(AggCustomerLtvId id) {
    this.id = id;
  }

  public Instant getFirstOrderProcessedAt() {
    return firstOrderProcessedAt;
  }

  public void setFirstOrderProcessedAt(Instant firstOrderProcessedAt) {
    this.firstOrderProcessedAt = firstOrderProcessedAt;
  }

  public Instant getLastOrderProcessedAt() {
    return lastOrderProcessedAt;
  }

  public void setLastOrderProcessedAt(Instant lastOrderProcessedAt) {
    this.lastOrderProcessedAt = lastOrderProcessedAt;
  }

  public Integer getLifetimeOrdersCount() {
    return lifetimeOrdersCount;
  }

  public void setLifetimeOrdersCount(Integer lifetimeOrdersCount) {
    this.lifetimeOrdersCount = lifetimeOrdersCount;
  }

  public BigDecimal getLifetimeRevenueGross() {
    return lifetimeRevenueGross;
  }

  public void setLifetimeRevenueGross(BigDecimal lifetimeRevenueGross) {
    this.lifetimeRevenueGross = lifetimeRevenueGross;
  }

  public BigDecimal getLifetimeNetSales() {
    return lifetimeNetSales;
  }

  public void setLifetimeNetSales(BigDecimal lifetimeNetSales) {
    this.lifetimeNetSales = lifetimeNetSales;
  }
}
