package com.ecom.analytics.dto;

public record OnboardRequest(String shopDomain, String clientId, String clientSecret, String adminEmail, String adminPassword) {}
