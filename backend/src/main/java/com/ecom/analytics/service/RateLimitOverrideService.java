package com.ecom.analytics.service;

import com.ecom.analytics.dto.RateLimitOverrideRequest;
import com.ecom.analytics.model.ShopRateLimit;
import com.ecom.analytics.repository.ShopRateLimitRepository;
import com.ecom.analytics.repository.ShopRepository;
import org.springframework.stereotype.Service;

@Service
public class RateLimitOverrideService {
  private final ShopRateLimitRepository shopRateLimitRepository;
  private final ShopRepository shopRepository;
  private final AuditService auditService;

  public RateLimitOverrideService(ShopRateLimitRepository shopRateLimitRepository,
                                  ShopRepository shopRepository,
                                  AuditService auditService) {
    this.shopRateLimitRepository = shopRateLimitRepository;
    this.shopRepository = shopRepository;
    this.auditService = auditService;
  }

  public void upsert(long shopId, RateLimitOverrideRequest request) {
    shopRepository.findByIdAndDeletedAtIsNull(shopId)
        .orElseThrow(() -> new IllegalArgumentException("Shop not found"));

    ShopRateLimit limit = shopRateLimitRepository.findById(shopId).orElseGet(() -> {
      ShopRateLimit l = new ShopRateLimit();
      l.setShopId(shopId);
      return l;
    });
    limit.setEnabled(request.enabled());
    limit.setCapacity(Math.max(1, request.capacity()));
    limit.setRefillPerMinute(Math.max(1, request.refillPerMinute()));
    shopRateLimitRepository.save(limit);

    auditService.record("RATE_LIMIT_UPDATED", null, shopId, java.util.Map.of(
        "enabled", limit.isEnabled(),
        "capacity", limit.getCapacity(),
        "refillPerMinute", limit.getRefillPerMinute()
    ));
  }
}
