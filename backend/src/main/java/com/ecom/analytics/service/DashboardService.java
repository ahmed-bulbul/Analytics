package com.ecom.analytics.service;

import com.ecom.analytics.dto.ChannelRow;
import com.ecom.analytics.dto.CohortRow;
import com.ecom.analytics.dto.GrowthResponse;
import com.ecom.analytics.dto.KpiResponse;
import com.ecom.analytics.dto.LtvResponse;
import com.ecom.analytics.repository.AggCustomerLtvRepository;
import com.ecom.analytics.repository.AggLtvCohortMonthRepository;
import com.ecom.analytics.repository.AggShopDayRepository;
import com.ecom.analytics.repository.AggShopDayWithSpendRepository;
import com.ecom.analytics.repository.FactChannelDayRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {
  private final AggShopDayWithSpendRepository aggShopDayWithSpendRepository;
  private final AggShopDayRepository aggShopDayRepository;
  private final AggCustomerLtvRepository aggCustomerLtvRepository;
  private final AggLtvCohortMonthRepository aggLtvCohortMonthRepository;
  private final FactChannelDayRepository factChannelDayRepository;

  public DashboardService(
      AggShopDayWithSpendRepository aggShopDayWithSpendRepository,
      AggShopDayRepository aggShopDayRepository,
      AggCustomerLtvRepository aggCustomerLtvRepository,
      AggLtvCohortMonthRepository aggLtvCohortMonthRepository,
      FactChannelDayRepository factChannelDayRepository
  ) {
    this.aggShopDayWithSpendRepository = aggShopDayWithSpendRepository;
    this.aggShopDayRepository = aggShopDayRepository;
    this.aggCustomerLtvRepository = aggCustomerLtvRepository;
    this.aggLtvCohortMonthRepository = aggLtvCohortMonthRepository;
    this.factChannelDayRepository = factChannelDayRepository;
  }

  public KpiResponse getKpis(long shopId, LocalDate from, LocalDate to) {
    var agg = aggShopDayWithSpendRepository.sumKpis(shopId, from, to);
    BigDecimal revenue = agg.getRevenueGross();
    BigDecimal netSales = agg.getNetSales();
    int orders = agg.getOrdersCount() == null ? 0 : agg.getOrdersCount();
    BigDecimal spend = agg.getAdSpendTotal();
    BigDecimal mer = safeDivide(revenue, spend, 6);
    BigDecimal aov = safeDivide(revenue, BigDecimal.valueOf(orders), 2);
    return new KpiResponse(revenue, netSales, orders, spend, mer, aov);
  }

  public GrowthResponse getGrowth(long shopId, LocalDate from, LocalDate to) {
    var agg = aggShopDayRepository.sumGrowth(shopId, from, to);
    return new GrowthResponse(
        agg.getNewOrders(),
        agg.getReturningOrders(),
        agg.getNewRevenue(),
        agg.getReturningRevenue()
    );
  }

  public LtvResponse getLtv(long shopId) {
    BigDecimal avgLtv = aggCustomerLtvRepository.avgLtv(shopId);
    BigDecimal ltv30 = fetchCohortLtvForMonth(shopId, 1);
    BigDecimal ltv60 = fetchCohortLtvForMonth(shopId, 2);
    BigDecimal ltv90 = fetchCohortLtvForMonth(shopId, 3);
    return new LtvResponse(avgLtv, ltv30, ltv60, ltv90);
  }

  private BigDecimal fetchCohortLtvForMonth(long shopId, int month) {
    var agg = aggLtvCohortMonthRepository.sumByMonth(shopId, month);
    BigDecimal totalLtv = agg.getTotalLtv();
    int customers = agg.getTotalCustomers() == null ? 0 : agg.getTotalCustomers();
    return safeDivide(totalLtv, BigDecimal.valueOf(customers), 2);
  }

  public List<CohortRow> getCohorts(long shopId, LocalDate from, LocalDate to) {
    List<Object[]> rows = aggLtvCohortMonthRepository.fetchCohorts(shopId, from, to);
    List<CohortRow> result = new ArrayList<>();
    for (Object[] row : rows) {
      result.add(new CohortRow(
          ((java.sql.Date) row[0]).toLocalDate(),
          ((Number) row[1]).intValue(),
          ((Number) row[2]).intValue(),
          (BigDecimal) row[3],
          (BigDecimal) row[4]
      ));
    }
    return result;
  }

  public List<ChannelRow> getChannels(long shopId, LocalDate from, LocalDate to) {
    List<Object[]> rows = factChannelDayRepository.fetchChannelRows(shopId, from, to);
    List<ChannelRow> result = new ArrayList<>();
    for (Object[] row : rows) {
      result.add(new ChannelRow(
          (String) row[0],
          (String) row[1],
          ((java.sql.Date) row[2]).toLocalDate(),
          (BigDecimal) row[3],
          ((Number) row[4]).longValue(),
          ((Number) row[5]).longValue(),
          ((Number) row[6]).intValue(),
          (BigDecimal) row[7]
      ));
    }
    return result;
  }

  private BigDecimal safeDivide(BigDecimal numerator, BigDecimal denominator, int scale) {
    if (denominator == null || denominator.compareTo(BigDecimal.ZERO) == 0) {
      return BigDecimal.ZERO;
    }
    if (numerator == null) {
      numerator = BigDecimal.ZERO;
    }
    return numerator.divide(denominator, scale, java.math.RoundingMode.HALF_UP);
  }
}
