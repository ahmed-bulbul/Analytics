package com.ecom.analytics.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "fact_orders")
public class FactOrder {
  @EmbeddedId
  private FactOrderId id;

  @Column(name = "order_name")
  private String orderName;

  @Column(name = "created_at")
  private Instant createdAt;

  @Column(name = "processed_at")
  private Instant processedAt;

  @Column(name = "updated_at")
  private Instant updatedAt;

  @Column(name = "cancelled_at")
  private Instant cancelledAt;

  @Column(name = "financial_status")
  private String financialStatus;

  @Column(name = "customer_gid")
  private String customerGid;

  @Column(name = "gross_total")
  private BigDecimal grossTotal;

  @Column(name = "gross_tax")
  private BigDecimal grossTax;

  @Column(name = "gross_shipping")
  private BigDecimal grossShipping;

  @Column(name = "net_sales")
  private BigDecimal netSales;

  @Column(name = "is_new_customer")
  private Boolean isNewCustomer;

  public FactOrderId getId() {
    return id;
  }

  public void setId(FactOrderId id) {
    this.id = id;
  }

  public String getOrderName() {
    return orderName;
  }

  public void setOrderName(String orderName) {
    this.orderName = orderName;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
  }

  public Instant getProcessedAt() {
    return processedAt;
  }

  public void setProcessedAt(Instant processedAt) {
    this.processedAt = processedAt;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(Instant updatedAt) {
    this.updatedAt = updatedAt;
  }

  public Instant getCancelledAt() {
    return cancelledAt;
  }

  public void setCancelledAt(Instant cancelledAt) {
    this.cancelledAt = cancelledAt;
  }

  public String getFinancialStatus() {
    return financialStatus;
  }

  public void setFinancialStatus(String financialStatus) {
    this.financialStatus = financialStatus;
  }

  public String getCustomerGid() {
    return customerGid;
  }

  public void setCustomerGid(String customerGid) {
    this.customerGid = customerGid;
  }

  public BigDecimal getGrossTotal() {
    return grossTotal;
  }

  public void setGrossTotal(BigDecimal grossTotal) {
    this.grossTotal = grossTotal;
  }

  public BigDecimal getGrossTax() {
    return grossTax;
  }

  public void setGrossTax(BigDecimal grossTax) {
    this.grossTax = grossTax;
  }

  public BigDecimal getGrossShipping() {
    return grossShipping;
  }

  public void setGrossShipping(BigDecimal grossShipping) {
    this.grossShipping = grossShipping;
  }

  public BigDecimal getNetSales() {
    return netSales;
  }

  public void setNetSales(BigDecimal netSales) {
    this.netSales = netSales;
  }

  public Boolean getIsNewCustomer() {
    return isNewCustomer;
  }

  public void setIsNewCustomer(Boolean isNewCustomer) {
    this.isNewCustomer = isNewCustomer;
  }
}
