package com.ecom.analytics.dto;

public record RegisterRequest(String email, String password, String role, Long shopId) {}
