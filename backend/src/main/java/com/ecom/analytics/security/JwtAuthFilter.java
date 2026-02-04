package com.ecom.analytics.security;

import com.ecom.analytics.service.JwtService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
  private final JwtService jwtService;

  public JwtAuthFilter(JwtService jwtService) {
    this.jwtService = jwtService;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    String auth = request.getHeader("Authorization");
    if (auth != null && auth.startsWith("Bearer ")) {
      String token = auth.substring(7);
      try {
        Claims claims = jwtService.parse(token);
        String email = claims.getSubject();
        long shopId = claims.get("shopId", Number.class).longValue();
        String role = claims.get("role", String.class);
        if (role == null || role.isBlank()) {
          role = "VIEWER";
        }
        long userId = claims.get("userId", Number.class).longValue();
        AuthPrincipal principal = new AuthPrincipal(email, shopId, role, userId);
        var authority = new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_" + role);
        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(principal, null, List.of(authority));
        SecurityContextHolder.getContext().setAuthentication(authentication);
      } catch (Exception ignored) {
        SecurityContextHolder.clearContext();
      }
    }
    filterChain.doFilter(request, response);
  }
}
