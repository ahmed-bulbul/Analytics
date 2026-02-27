package com.ecom.analytics.dto;

/**
 * DTO representing platform-agnostic customer data for analytics
 * Bridges Shopify/WooCommerce/Magento customer structures to common format
 */
public record CustomerDimensionDto(
    Long shopId,
    String platformType,
    String sourceCustomerId,
    String email,
    String emailHash,
    String createdAt,
    String updatedAt,
    String firstOrderProcessedAt,
    String lastOrderProcessedAt,
    Boolean hasEmail
) {}
