package com.ecom.analytics.dto;

import java.math.BigDecimal;

public record GrowthResponse(
    Integer newOrders,
    Integer returningOrders,
    BigDecimal newRevenue,
    BigDecimal returningRevenue
) {}
