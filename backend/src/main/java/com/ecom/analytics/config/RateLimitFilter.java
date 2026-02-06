package com.ecom.analytics.config;

import com.ecom.analytics.repository.ShopRateLimitRepository;
import com.ecom.analytics.security.AuthPrincipal;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class RateLimitFilter extends OncePerRequestFilter {
  private final RateLimitProperties properties;
  private final ShopRateLimitRepository shopRateLimitRepository;
  private final ConcurrentHashMap<String, Bucket> buckets = new ConcurrentHashMap<>();
  private final ObjectMapper mapper = new ObjectMapper();

  public RateLimitFilter(RateLimitProperties properties, ShopRateLimitRepository shopRateLimitRepository) {
    this.properties = properties;
    this.shopRateLimitRepository = shopRateLimitRepository;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    if (!properties.isEnabled() || !request.getRequestURI().startsWith("/api")) {
      filterChain.doFilter(request, response);
      return;
    }

    int capacity = properties.getCapacity();
    int refill = properties.getRefillPerMinute();
    boolean enabled = properties.isEnabled();

    AuthPrincipal principal = currentPrincipal();
    if (principal != null) {
      var override = shopRateLimitRepository.findById(principal.shopId());
      if (override.isPresent()) {
        var limit = override.get();
        enabled = limit.isEnabled();
        capacity = limit.getCapacity();
        refill = limit.getRefillPerMinute();
      }
    }
    if (!enabled) {
      filterChain.doFilter(request, response);
      return;
    }

    String key = principal != null ? ("shop:" + principal.shopId()) : ("ip:" + clientKey(request));
    final int finalCapacity = capacity;
    final int finalRefill = refill;
    Bucket bucket = buckets.computeIfAbsent(key, k -> new Bucket(finalCapacity, finalRefill));
    if (!bucket.allow()) {
      response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
      response.setContentType(MediaType.APPLICATION_JSON_VALUE);
      Map<String, Object> payload = Map.of(
          "timestamp", Instant.now().toString(),
          "status", 429,
          "error", "Too Many Requests",
          "message", "Rate limit exceeded"
      );
      response.getWriter().write(mapper.writeValueAsString(payload));
      return;
    }

    filterChain.doFilter(request, response);
  }

  private AuthPrincipal currentPrincipal() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth != null && auth.getPrincipal() instanceof AuthPrincipal principal) {
      return principal;
    }
    return null;
  }

  private String clientKey(HttpServletRequest request) {
    String forwarded = request.getHeader("X-Forwarded-For");
    if (forwarded != null && !forwarded.isBlank()) {
      int idx = forwarded.indexOf(',');
      return (idx >= 0 ? forwarded.substring(0, idx) : forwarded).trim();
    }
    String realIp = request.getHeader("X-Real-IP");
    if (realIp != null && !realIp.isBlank()) {
      return realIp.trim();
    }
    return request.getRemoteAddr();
  }

  private static class Bucket {
    private final int capacity;
    private final int refillPerMinute;
    private double tokens;
    private long lastRefillMs;

    Bucket(int capacity, int refillPerMinute) {
      this.capacity = capacity;
      this.refillPerMinute = refillPerMinute;
      this.tokens = capacity;
      this.lastRefillMs = System.currentTimeMillis();
    }

    synchronized boolean allow() {
      refill();
      if (tokens >= 1) {
        tokens -= 1;
        return true;
      }
      return false;
    }

    private void refill() {
      long now = System.currentTimeMillis();
      long elapsedMs = now - lastRefillMs;
      if (elapsedMs <= 0) return;
      double tokensToAdd = (elapsedMs / 60000.0) * refillPerMinute;
      if (tokensToAdd > 0) {
        tokens = Math.min(capacity, tokens + tokensToAdd);
        lastRefillMs = now;
      }
    }
  }
}
