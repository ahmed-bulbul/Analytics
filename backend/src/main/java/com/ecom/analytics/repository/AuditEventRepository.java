package com.ecom.analytics.repository;

import com.ecom.analytics.dto.AuditEventRow;
import com.ecom.analytics.model.AuditEvent;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AuditEventRepository extends JpaRepository<AuditEvent, Long> {
  @Query("""
      select new com.ecom.analytics.dto.AuditEventRow(
        e.id, e.action, e.actorUserId, e.actorEmail, e.targetUserId, e.targetShopId,
        e.metadata, e.ipAddress, e.userAgent, e.createdAt
      )
      from AuditEvent e
      where (:action is null or lower(e.action) like lower(concat('%', :action, '%')))
        and (:shopId is null or e.targetShopId = :shopId)
      order by e.createdAt desc
      """)
  List<AuditEventRow> findAuditRows(@Param("action") String action, @Param("shopId") Long shopId, Pageable pageable);

  @Query("""
      select count(e.id)
      from AuditEvent e
      where (:action is null or lower(e.action) like lower(concat('%', :action, '%')))
        and (:shopId is null or e.targetShopId = :shopId)
      """)
  long countAuditRows(@Param("action") String action, @Param("shopId") Long shopId);
}
