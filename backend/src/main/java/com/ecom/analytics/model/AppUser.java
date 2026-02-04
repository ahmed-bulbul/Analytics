package com.ecom.analytics.model;

public record AppUser(
    long id,
    String email,
    String passwordHash,
    long primaryShopId,
    String role
) {}
