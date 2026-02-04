package com.ecom.analytics.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.ecom.analytics.model.AggShopDay;
import com.ecom.analytics.model.AggShopDayId;

public interface AggShopDayRepository extends JpaRepository<AggShopDay, AggShopDayId> {
  interface GrowthAgg {
    Integer getNewOrders();
    Integer getReturningOrders();
    BigDecimal getNewRevenue();
    BigDecimal getReturningRevenue();
  }

  @Query(value = """
      select
        coalesce(sum(new_orders_count), 0) as newOrders,
        coalesce(sum(returning_orders_count), 0) as returningOrders,
        coalesce(sum(new_revenue_gross), 0) as newRevenue,
        coalesce(sum(returning_revenue_gross), 0) as returningRevenue
      from agg_shop_day
      where shop_id = :shopId and date between :from and :to
      """, nativeQuery = true)
  GrowthAgg sumGrowth(@Param("shopId") long shopId, @Param("from") LocalDate from, @Param("to") LocalDate to);
}
