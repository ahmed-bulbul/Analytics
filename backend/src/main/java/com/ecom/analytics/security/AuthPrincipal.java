package com.ecom.analytics.security;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
public record AuthPrincipal(
        String email,
        long shopId,
        String role,
        long userId
) {}