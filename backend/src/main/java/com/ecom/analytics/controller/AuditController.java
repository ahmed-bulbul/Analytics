package com.ecom.analytics.controller;

import com.ecom.analytics.dto.AuditEventRow;
import com.ecom.analytics.dto.PageResult;
import com.ecom.analytics.service.AuditQueryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/audit")
public class AuditController {
  private final AuditQueryService auditQueryService;

  public AuditController(AuditQueryService auditQueryService) {
    this.auditQueryService = auditQueryService;
  }

  @GetMapping
  public ResponseEntity<PageResult<AuditEventRow>> list(
      @RequestParam(required = false) String action,
      @RequestParam(required = false) Long shopId,
      @RequestParam(defaultValue = "50") int limit,
      @RequestParam(defaultValue = "0") int offset
  ) {
    int normalizedLimit = Math.min(Math.max(limit, 1), 200);
    int normalizedOffset = Math.max(offset, 0);
    return ResponseEntity.ok(auditQueryService.list(action, shopId, normalizedLimit, normalizedOffset));
  }
}
