package com.ecom.analytics.repository;

import com.ecom.analytics.model.ShopRateLimit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShopRateLimitRepository extends JpaRepository<ShopRateLimit, Long> {
}
