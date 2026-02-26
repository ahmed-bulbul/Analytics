package com.ecom.analytics.security;
package com.ecom.analytics.security;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthPrincipal {
    private final String email;
    private final long shopId;
    private final String role;
    private final long userId;
}