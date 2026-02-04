package com.ecom.analytics.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CohortRow(
    LocalDate cohortMonth,
    Integer monthsSinceFirstOrder,
    Integer customersInCohort,
    BigDecimal netSales,
    BigDecimal cumulativeNetSales
) {}
