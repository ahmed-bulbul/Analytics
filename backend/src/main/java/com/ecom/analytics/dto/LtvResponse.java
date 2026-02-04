package com.ecom.analytics.dto;

import java.math.BigDecimal;

public record LtvResponse(
    BigDecimal averageCustomerLtv,
    BigDecimal ltv30,
    BigDecimal ltv60,
    BigDecimal ltv90
) {}
