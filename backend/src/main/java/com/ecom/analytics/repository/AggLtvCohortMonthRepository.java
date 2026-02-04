package com.ecom.analytics.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.ecom.analytics.model.AggLtvCohortMonth;
import com.ecom.analytics.model.AggLtvCohortMonthId;
import com.ecom.analytics.dto.CohortRow;

public interface AggLtvCohortMonthRepository extends JpaRepository<AggLtvCohortMonth, AggLtvCohortMonthId> {
  @Query(value = """
      select
        coalesce(sum(cumulative_net_sales), 0) as totalLtv,
        coalesce(sum(customers_in_cohort), 0) as totalCustomers
      from agg_ltv_cohort_month
      where shop_id = :shopId and months_since_first_order = :month
      """, nativeQuery = true)
  LtvMonthAgg sumByMonth(@Param("shopId") long shopId, @Param("month") int month);

  @Query(value = """
      select cohort_month, months_since_first_order, customers_in_cohort, net_sales, cumulative_net_sales
      from agg_ltv_cohort_month
      where shop_id = :shopId and cohort_month between :from and :to
      order by cohort_month asc, months_since_first_order asc
      """, nativeQuery = true)
  List<Object[]> fetchCohorts(@Param("shopId") long shopId, @Param("from") LocalDate from, @Param("to") LocalDate to);

  interface LtvMonthAgg {
    BigDecimal getTotalLtv();
    Integer getTotalCustomers();
  }
}
