package com.ecom.analytics.service;

import com.ecom.analytics.model.Shop;
import com.ecom.analytics.model.User;
import com.ecom.analytics.repository.ShopRepository;
import com.ecom.analytics.repository.UserRepository;
import java.time.Instant;
import org.springframework.stereotype.Service;

@Service
public class AdminSoftDeleteService {
  private final UserRepository userRepository;
  private final ShopRepository shopRepository;
  private final AuditService auditService;

  public AdminSoftDeleteService(UserRepository userRepository, ShopRepository shopRepository, AuditService auditService) {
    this.userRepository = userRepository;
    this.shopRepository = shopRepository;
    this.auditService = auditService;
  }

  public void softDeleteUser(long userId, long actorUserId) {
    User user = userRepository.findByIdAndDeletedAtIsNull(userId)
        .orElseThrow(() -> new IllegalArgumentException("User not found"));
    user.setDeletedAt(Instant.now());
    user.setDeletedBy(actorUserId);
    userRepository.save(user);
    auditService.record("USER_SOFT_DELETED", userId, user.getPrimaryShop().getId(), java.util.Map.of("email", user.getEmail()));
  }

  public void softDeleteShop(long shopId, long actorUserId) {
    Shop shop = shopRepository.findByIdAndDeletedAtIsNull(shopId)
        .orElseThrow(() -> new IllegalArgumentException("Shop not found"));
    shop.setDeletedAt(Instant.now());
    shop.setDeletedBy(actorUserId);
    shopRepository.save(shop);
    auditService.record("SHOP_SOFT_DELETED", null, shopId, java.util.Map.of("shopDomain", shop.getShopDomain()));
  }
}
