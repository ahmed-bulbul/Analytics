package com.ecom.analytics.security;

public record AuthPrincipal(String email, long shopId, String role, long userId) {}
