package com.ecom.analytics.security;

import com.ecom.analytics.security.AuthPrincipal;
import com.ecom.analytics.service.JwtService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
  private final JwtService jwtService;

  public JwtAuthFilter(JwtService jwtService) {
    this.jwtService = jwtService;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
          throws ServletException, IOException {

    String authHeader = request.getHeader("Authorization");

    if (authHeader != null && authHeader.startsWith("Bearer ")) {
      String token = authHeader.substring(7);
      try {
        Claims claims = jwtService.parse(token);

        // 1. Extract and normalize data
        String email = claims.getSubject();
        String role = claims.get("role", String.class);
        if (role == null || role.isBlank()) {
          role = "VIEWER";
        }

        // Use Number.class to safely handle Integer/Long types from JSON
        long shopId = claims.get("shopId", Number.class).longValue();
        long userId = claims.get("userId", Number.class).longValue();

        // 2. Create our custom Principal object
        AuthPrincipal principal = new AuthPrincipal(email, shopId, role, userId);

        // 3. Map role to Spring Security Authority
        var authority = new SimpleGrantedAuthority("ROLE_" + role);

        // 4. Set the Authentication Context
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(principal, null, List.of(authority));

        SecurityContextHolder.getContext().setAuthentication(authentication);

      } catch (Exception e) {
        // Clear context on invalid token to ensure security
        SecurityContextHolder.clearContext();
      }
    }

    filterChain.doFilter(request, response);
  }
}
