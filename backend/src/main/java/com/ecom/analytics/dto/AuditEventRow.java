package com.ecom.analytics.dto;

import java.time.Instant;

public record AuditEventRow(
    Long id,
    String action,
    Long actorUserId,
    String actorEmail,
    Long targetUserId,
    Long targetShopId,
    String metadata,
    String ipAddress,
    String userAgent,
    Instant createdAt
) {}
