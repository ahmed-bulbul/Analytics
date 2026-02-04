package com.ecom.analytics.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.ecom.analytics.model.AggShopDayWithSpend;
import com.ecom.analytics.model.AggShopDayWithSpendId;

public interface AggShopDayWithSpendRepository extends JpaRepository<AggShopDayWithSpend, AggShopDayWithSpendId> {
  interface KpiAgg {
    BigDecimal getRevenueGross();
    BigDecimal getNetSales();
    Integer getOrdersCount();
    BigDecimal getAdSpendTotal();
  }

  @Query(value = """
      select
        coalesce(sum(revenue_gross), 0) as revenueGross,
        coalesce(sum(net_sales), 0) as netSales,
        coalesce(sum(orders_count), 0) as ordersCount,
        coalesce(sum(ad_spend_total), 0) as adSpendTotal
      from agg_shop_day_with_spend
      where shop_id = :shopId and date between :from and :to
      """, nativeQuery = true)
  KpiAgg sumKpis(@Param("shopId") long shopId, @Param("from") LocalDate from, @Param("to") LocalDate to);
}
