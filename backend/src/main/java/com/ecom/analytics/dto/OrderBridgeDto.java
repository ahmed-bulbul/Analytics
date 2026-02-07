package com.ecom.analytics.dto;

/**
 * DTO representing platform-agnostic order data for analytics
 * Bridges Shopify/WooCommerce/Magento order structures to common format
 */
public record OrderBridgeDto(
    Long shopId,
    String platformType,
    String sourceOrderId,
    String orderDisplayName,
    String createdAt,
    String processedAt,
    String updatedAt,
    String cancelledAt,
    String financialStatus,
    String sourceCustomerId,
    Double grossTotal,
    Double grossTax,
    Double grossShipping,
    Double netSales,
    Boolean isNewCustomer
) {}
