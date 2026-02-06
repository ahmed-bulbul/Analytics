package com.ecom.analytics.service;

import com.ecom.analytics.dto.GrantAccessRequest;
import com.ecom.analytics.dto.GrantAccessResponse;
import com.ecom.analytics.dto.PageResult;
import com.ecom.analytics.dto.RevokeAccessRequest;
import com.ecom.analytics.dto.RevokeAccessResponse;
import com.ecom.analytics.dto.ShopRow;
import com.ecom.analytics.dto.UserShopRow;
import com.ecom.analytics.model.Shop;
import com.ecom.analytics.model.User;
import com.ecom.analytics.model.UserShop;
import com.ecom.analytics.repository.ShopRepository;
import com.ecom.analytics.repository.UserRepository;
import com.ecom.analytics.repository.UserShopRepository;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class AccessService {
  private final UserShopRepository userShopRepository;
  private final UserRepository userRepository;
  private final ShopRepository shopRepository;
  private final AuditService auditService;

  public AccessService(UserShopRepository userShopRepository,
                       UserRepository userRepository,
                       ShopRepository shopRepository,
                       AuditService auditService) {
    this.userShopRepository = userShopRepository;
    this.userRepository = userRepository;
    this.shopRepository = shopRepository;
    this.auditService = auditService;
  }

  public GrantAccessResponse grantAccess(GrantAccessRequest request) {
    User user = userRepository.findByIdAndDeletedAtIsNull(request.userId())
        .orElseThrow(() -> new IllegalArgumentException("User not found"));
    Shop shop = shopRepository.findByIdAndDeletedAtIsNull(request.shopId())
        .orElseThrow(() -> new IllegalArgumentException("Shop not found"));
    userShopRepository.save(new UserShop(user, shop));
    auditService.record("ACCESS_GRANTED", user.getId(), shop.getId(), java.util.Map.of(
        "userEmail", user.getEmail(),
        "shopDomain", shop.getShopDomain()
    ));
    return new GrantAccessResponse(request.userId(), request.shopId(), "granted");
  }

  public RevokeAccessResponse revokeAccess(RevokeAccessRequest request) {
    userShopRepository.deleteById(new com.ecom.analytics.model.UserShopId(request.userId(), request.shopId()));
    auditService.record("ACCESS_REVOKED", request.userId(), request.shopId(), java.util.Map.of());
    return new RevokeAccessResponse(request.userId(), request.shopId(), "revoked");
  }

  public PageResult<ShopRow> listShopsForUser(long userId, String domainFilter, int limit, int offset, String sortDir) {
    var pageable = PageRequest.of(offset / limit, limit);
    List<ShopRow> items = userShopRepository.findShopsForUser(userId, domainFilter, pageable);
    long total = userShopRepository.countShopsForUser(userId, domainFilter);
    return new PageResult<>(items, total, limit, offset, null);
  }

  public PageResult<UserShopRow> listUsersForShop(long shopId, String emailFilter, int limit, int offset, String sortDir) {
    var pageable = PageRequest.of(offset / limit, limit);
    List<UserShopRow> items = userShopRepository.findUsersForShop(shopId, emailFilter, pageable);
    long total = userShopRepository.countUsersForShop(shopId, emailFilter);
    return new PageResult<>(items, total, limit, offset, null);
  }

  public PageResult<UserShopRow> listUsersForShopCursor(long shopId, String emailFilter, int limit, String sortDir, String cursor) {
    List<UserShopRow> items;
    var pageable = PageRequest.of(0, limit);
    if (cursor == null || cursor.isBlank()) {
      items = userShopRepository.findUsersForShop(shopId, emailFilter, pageable);
    } else {
      CursorParts parts = decodeCursor(cursor);
      if ("DESC".equalsIgnoreCase(sortDir)) {
        items = userShopRepository.findUsersForShopCursorDesc(shopId, emailFilter, parts.value(), parts.id(), pageable);
      } else {
        items = userShopRepository.findUsersForShopCursorAsc(shopId, emailFilter, parts.value(), parts.id(), pageable);
      }
    }
    String nextCursor = buildUserCursor(items);
    return new PageResult<>(items, null, limit, null, nextCursor);
  }

  public PageResult<ShopRow> listShopsForUserCursor(long userId, String domainFilter, int limit, String sortDir, String cursor) {
    List<ShopRow> items;
    var pageable = PageRequest.of(0, limit);
    if (cursor == null || cursor.isBlank()) {
      items = userShopRepository.findShopsForUser(userId, domainFilter, pageable);
    } else {
      CursorParts parts = decodeCursor(cursor);
      if ("DESC".equalsIgnoreCase(sortDir)) {
        items = userShopRepository.findShopsForUserCursorDesc(userId, domainFilter, parts.value(), parts.id(), pageable);
      } else {
        items = userShopRepository.findShopsForUserCursorAsc(userId, domainFilter, parts.value(), parts.id(), pageable);
      }
    }
    String nextCursor = buildShopCursor(items);
    return new PageResult<>(items, null, limit, null, nextCursor);
  }

  public boolean hasAccess(long userId, long shopId) {
    return userShopRepository.existsByIdUserIdAndIdShopId(userId, shopId);
  }

  private String buildUserCursor(List<UserShopRow> items) {
    if (items.isEmpty()) {
      return null;
    }
    UserShopRow last = items.get(items.size() - 1);
    return encodeCursor(last.email(), last.userId());
  }

  private String buildShopCursor(List<ShopRow> items) {
    if (items.isEmpty()) {
      return null;
    }
    ShopRow last = items.get(items.size() - 1);
    return encodeCursor(last.shopDomain(), last.shopId());
  }

  private String encodeCursor(String value, long id) {
    String raw = value + "|" + id;
    return Base64.getUrlEncoder().encodeToString(raw.getBytes(StandardCharsets.UTF_8));
  }

  private CursorParts decodeCursor(String cursor) {
    if (cursor == null || cursor.isBlank()) {
      return new CursorParts("", 0);
    }
    String raw = new String(Base64.getUrlDecoder().decode(cursor), StandardCharsets.UTF_8);
    int idx = raw.lastIndexOf('|');
    if (idx < 0) {
      return new CursorParts(raw, 0);
    }
    String value = raw.substring(0, idx);
    long id = Long.parseLong(raw.substring(idx + 1));
    return new CursorParts(value, id);
  }

  private record CursorParts(String value, long id) {}
}
