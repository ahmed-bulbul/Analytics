package com.ecom.analytics.dto;

/**
 * DTO representing a shop dimension for analytics/OLAP systems (Doris, etc.)
 * This provides a clean abstraction for multi-platform e-commerce analytics
 */
public record ShopDimensionDto(
    Long shopId,
    String shopDomain,
    String platformType,  // shopify, woocommerce, magento
    String platformShopId,
    String shopName,
    String shopOwnerName,
    String shopOwnerEmail,
    String currency,
    String timezone,
    String countryCode,
    String shopPlanName,
    Boolean isActive,
    String createdAt,
    String updatedAt
) {}
