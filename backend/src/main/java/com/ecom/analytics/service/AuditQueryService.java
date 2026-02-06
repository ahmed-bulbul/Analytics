package com.ecom.analytics.service;

import com.ecom.analytics.dto.AuditEventRow;
import com.ecom.analytics.dto.PageResult;
import com.ecom.analytics.repository.AuditEventRepository;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class AuditQueryService {
  private final AuditEventRepository auditEventRepository;

  public AuditQueryService(AuditEventRepository auditEventRepository) {
    this.auditEventRepository = auditEventRepository;
  }

  public PageResult<AuditEventRow> list(String action, Long shopId, int limit, int offset) {
    var pageable = PageRequest.of(offset / limit, limit);
    List<AuditEventRow> items = auditEventRepository.findAuditRows(action, shopId, pageable);
    long total = auditEventRepository.countAuditRows(action, shopId);
    return new PageResult<>(items, total, limit, offset, null);
  }
}
