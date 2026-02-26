package com.ecom.analytics.security;

import com.ecom.analytics.model.User;
import com.ecom.analytics.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
public class SecurityContextProvider {

    private final UserRepository userRepository;

    /**
     * @return Optional containing the AuthPrincipal if the user is authenticated.
     */
    public Optional<AuthPrincipal> getPrincipal() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof AuthPrincipal) {
            return Optional.of((AuthPrincipal) auth.getPrincipal());
        }
        return Optional.empty();
    }

    public Long getCurrentUserId() {
        return getPrincipal().map(AuthPrincipal::getUserId).orElse(null);
    }

    public Long getCurrentShopId() {
        return getPrincipal().map(AuthPrincipal::getShopId).orElse(null);
    }

    public Optional<User> getUser(){
        return userRepository.findById(getCurrentUserId());
    }
}