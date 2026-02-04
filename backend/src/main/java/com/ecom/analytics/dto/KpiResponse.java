package com.ecom.analytics.dto;

import java.math.BigDecimal;

public record KpiResponse(
    BigDecimal revenueGross,
    BigDecimal netSales,
    Integer orders,
    BigDecimal adSpend,
    BigDecimal mer,
    BigDecimal aov
) {}
