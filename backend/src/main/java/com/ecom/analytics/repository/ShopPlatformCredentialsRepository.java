package com.ecom.analytics.repository;

import com.ecom.analytics.model.ShopPlatformCredentials;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShopPlatformCredentialsRepository extends JpaRepository<ShopPlatformCredentials, Long> {
  Optional<ShopPlatformCredentials> findByShopIdAndPlatformType(Long shopId, String platformType);
}
