package com.ecom.analytics.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ChannelRow(
    String channelKey,
    String channelName,
    LocalDate date,
    BigDecimal spend,
    Long impressions,
    Long clicks,
    Integer attributedOrders,
    BigDecimal attributedRevenue
) {}
