package com.ecom.analytics.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "agg_ltv_cohort_month")
public class AggLtvCohortMonth {
  @EmbeddedId
  private AggLtvCohortMonthId id;

  @Column(name = "customers_in_cohort", nullable = false)
  private Integer customersInCohort;

  @Column(name = "net_sales", nullable = false)
  private BigDecimal netSales;

  @Column(name = "cumulative_net_sales", nullable = false)
  private BigDecimal cumulativeNetSales;

  public AggLtvCohortMonthId getId() {
    return id;
  }

  public void setId(AggLtvCohortMonthId id) {
    this.id = id;
  }

  public Integer getCustomersInCohort() {
    return customersInCohort;
  }

  public void setCustomersInCohort(Integer customersInCohort) {
    this.customersInCohort = customersInCohort;
  }

  public BigDecimal getNetSales() {
    return netSales;
  }

  public void setNetSales(BigDecimal netSales) {
    this.netSales = netSales;
  }

  public BigDecimal getCumulativeNetSales() {
    return cumulativeNetSales;
  }

  public void setCumulativeNetSales(BigDecimal cumulativeNetSales) {
    this.cumulativeNetSales = cumulativeNetSales;
  }
}
