package com.ecom.analytics.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.Collections;

public class UserPrincipal extends UsernamePasswordAuthenticationToken {
    private final Long userId;
    private final Long shopId;

    public UserPrincipal(String email, Long userId, Long shopId, String role) {
        super(email, null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role)));
        this.userId = userId;
        this.shopId = shopId;
    }

    public Long getUserId() { return userId; }
    public Long getShopId() { return shopId; }
}
