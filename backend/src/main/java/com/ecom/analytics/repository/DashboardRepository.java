package com.ecom.analytics.repository;

import com.ecom.analytics.dto.ChannelRow;
import com.ecom.analytics.dto.CohortRow;
import com.ecom.analytics.dto.GrowthResponse;
import com.ecom.analytics.dto.KpiResponse;
import com.ecom.analytics.dto.LtvResponse;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class DashboardRepository {
  private final JdbcTemplate jdbcTemplate;

  public DashboardRepository(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  public KpiResponse fetchKpis(long shopId, LocalDate from, LocalDate to) {
    String sql = """
        SELECT
          COALESCE(SUM(revenue_gross), 0) AS revenue_gross,
          COALESCE(SUM(net_sales), 0) AS net_sales,
          COALESCE(SUM(orders_count), 0) AS orders_count,
          COALESCE(SUM(ad_spend_total), 0) AS ad_spend_total
        FROM agg_shop_day_with_spend
        WHERE shop_id = ? AND date >= ? AND date <= ?
        """;

    return jdbcTemplate.queryForObject(
        sql,
        (rs, rowNum) -> {
          BigDecimal revenue = rs.getBigDecimal("revenue_gross");
          BigDecimal netSales = rs.getBigDecimal("net_sales");
          int orders = rs.getInt("orders_count");
          BigDecimal spend = rs.getBigDecimal("ad_spend_total");

          BigDecimal mer = safeDivide(revenue, spend, 6);
          BigDecimal aov = safeDivide(revenue, BigDecimal.valueOf(orders), 2);

          return new KpiResponse(revenue, netSales, orders, spend, mer, aov);
        },
        shopId,
        Date.valueOf(from),
        Date.valueOf(to)
    );
  }

  public GrowthResponse fetchGrowth(long shopId, LocalDate from, LocalDate to) {
    String sql = """
        SELECT
          COALESCE(SUM(new_orders_count), 0) AS new_orders,
          COALESCE(SUM(returning_orders_count), 0) AS returning_orders,
          COALESCE(SUM(new_revenue_gross), 0) AS new_revenue,
          COALESCE(SUM(returning_revenue_gross), 0) AS returning_revenue
        FROM agg_shop_day
        WHERE shop_id = ? AND date >= ? AND date <= ?
        """;

    return jdbcTemplate.queryForObject(
        sql,
        (rs, rowNum) -> new GrowthResponse(
            rs.getInt("new_orders"),
            rs.getInt("returning_orders"),
            rs.getBigDecimal("new_revenue"),
            rs.getBigDecimal("returning_revenue")
        ),
        shopId,
        Date.valueOf(from),
        Date.valueOf(to)
    );
  }

  public LtvResponse fetchLtv(long shopId) {
    String sqlAvg = """
        SELECT COALESCE(AVG(lifetime_net_sales), 0) AS avg_ltv
        FROM agg_customer_ltv
        WHERE shop_id = ?
        """;

    BigDecimal avgLtv = jdbcTemplate.queryForObject(sqlAvg, BigDecimal.class, shopId);

    BigDecimal ltv30 = fetchCohortLtvForMonth(shopId, 1);
    BigDecimal ltv60 = fetchCohortLtvForMonth(shopId, 2);
    BigDecimal ltv90 = fetchCohortLtvForMonth(shopId, 3);

    return new LtvResponse(avgLtv, ltv30, ltv60, ltv90);
  }

  private BigDecimal fetchCohortLtvForMonth(long shopId, int month) {
    String sql = """
        SELECT
          COALESCE(SUM(cumulative_net_sales), 0) AS total_ltv,
          COALESCE(SUM(customers_in_cohort), 0) AS total_customers
        FROM agg_ltv_cohort_month
        WHERE shop_id = ? AND months_since_first_order = ?
        """;

    return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
      BigDecimal totalLtv = rs.getBigDecimal("total_ltv");
      int customers = rs.getInt("total_customers");
      return safeDivide(totalLtv, BigDecimal.valueOf(customers), 2);
    }, shopId, month);
  }

  public List<CohortRow> fetchCohorts(long shopId, LocalDate from, LocalDate to) {
    String sql = """
        SELECT cohort_month, months_since_first_order, customers_in_cohort, net_sales, cumulative_net_sales
        FROM agg_ltv_cohort_month
        WHERE shop_id = ? AND cohort_month >= ? AND cohort_month <= ?
        ORDER BY cohort_month ASC, months_since_first_order ASC
        """;

    RowMapper<CohortRow> mapper = (rs, rowNum) -> new CohortRow(
        rs.getDate("cohort_month").toLocalDate(),
        rs.getInt("months_since_first_order"),
        rs.getInt("customers_in_cohort"),
        rs.getBigDecimal("net_sales"),
        rs.getBigDecimal("cumulative_net_sales")
    );

    return jdbcTemplate.query(sql, mapper, shopId, Date.valueOf(from), Date.valueOf(to));
  }

  public List<ChannelRow> fetchChannels(long shopId, LocalDate from, LocalDate to) {
    String sql = """
        SELECT c.channel_key, c.channel_name, f.date, f.spend, f.impressions, f.clicks,
               f.attributed_orders, f.attributed_revenue
        FROM fact_channel_day f
        JOIN dim_channel c ON c.channel_id = f.channel_id
        WHERE f.shop_id = ? AND f.date >= ? AND f.date <= ?
        ORDER BY f.date ASC, c.channel_key ASC
        """;

    RowMapper<ChannelRow> mapper = (rs, rowNum) -> new ChannelRow(
        rs.getString("channel_key"),
        rs.getString("channel_name"),
        rs.getDate("date").toLocalDate(),
        rs.getBigDecimal("spend"),
        rs.getLong("impressions"),
        rs.getLong("clicks"),
        rs.getInt("attributed_orders"),
        rs.getBigDecimal("attributed_revenue")
    );

    return jdbcTemplate.query(sql, mapper, shopId, Date.valueOf(from), Date.valueOf(to));
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
