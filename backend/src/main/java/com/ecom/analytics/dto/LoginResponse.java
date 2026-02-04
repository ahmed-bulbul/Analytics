package com.ecom.analytics.dto;

public record LoginResponse(String token, long shopId, String email) {}
