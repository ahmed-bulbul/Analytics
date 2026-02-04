package com.ecom.analytics.repository;

import java.math.BigDecimal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.ecom.analytics.model.AggCustomerLtv;
import com.ecom.analytics.model.AggCustomerLtvId;

public interface AggCustomerLtvRepository extends JpaRepository<AggCustomerLtv, AggCustomerLtvId> {
  @Query(value = """
      select coalesce(avg(lifetime_net_sales), 0)
      from agg_customer_ltv
      where shop_id = :shopId
      """, nativeQuery = true)
  BigDecimal avgLtv(@Param("shopId") long shopId);
}
