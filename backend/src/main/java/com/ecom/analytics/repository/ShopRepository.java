package com.ecom.analytics.repository;

import com.ecom.analytics.model.Shop;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShopRepository extends JpaRepository<Shop, Long> {
  Optional<Shop> findByShopDomainIgnoreCaseAndDeletedAtIsNull(String shopDomain);
  boolean existsByShopDomainIgnoreCaseAndDeletedAtIsNull(String shopDomain);
  java.util.List<Shop> findByShopifyAccessTokenEncryptedIsNotNullAndDeletedAtIsNull();
  Optional<Shop> findByIdAndDeletedAtIsNull(Long id);
}
