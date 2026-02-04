package com.ecom.analytics.repository;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.ecom.analytics.model.FactChannelDay;
import com.ecom.analytics.model.FactChannelDayId;
import com.ecom.analytics.dto.ChannelRow;

public interface FactChannelDayRepository extends JpaRepository<FactChannelDay, FactChannelDayId> {
  @Query(value = """
      select c.channel_key, c.channel_name, f.date, f.spend, f.impressions, f.clicks,
             f.attributed_orders, f.attributed_revenue
      from fact_channel_day f
      join dim_channel c on c.channel_id = f.channel_id
      where f.shop_id = :shopId and f.date between :from and :to
      order by f.date asc, c.channel_key asc
      """, nativeQuery = true)
  List<Object[]> fetchChannelRows(@Param("shopId") long shopId, @Param("from") LocalDate from, @Param("to") LocalDate to);
}
