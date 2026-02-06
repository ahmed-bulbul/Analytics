package com.ecom.analytics.dto;

import java.time.Instant;

public record SyncStatusResponse(
    long shopId,
    String status,
    String currentType,
    String operationId,
    Instant updatedAt,
    Instant lastIncrementalSyncAt
) {}
