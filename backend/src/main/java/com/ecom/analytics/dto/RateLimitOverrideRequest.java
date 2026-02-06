package com.ecom.analytics.dto;

public record RateLimitOverrideRequest(
    boolean enabled,
    int capacity,
    int refillPerMinute
) {}
