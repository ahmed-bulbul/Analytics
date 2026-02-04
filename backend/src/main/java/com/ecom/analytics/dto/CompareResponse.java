package com.ecom.analytics.dto;

public record CompareResponse(
    KpiResponse current,
    KpiResponse previous
) {}
