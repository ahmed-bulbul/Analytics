package com.ecom.analytics.dto;

public record RegisterResponse(long userId, String email, String role, long shopId) {}
