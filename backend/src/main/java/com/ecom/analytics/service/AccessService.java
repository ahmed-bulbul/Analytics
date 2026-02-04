package com.ecom.analytics.service;

import com.ecom.analytics.dto.GrantAccessRequest;
import com.ecom.analytics.dto.GrantAccessResponse;
import com.ecom.analytics.dto.RevokeAccessRequest;
import com.ecom.analytics.dto.RevokeAccessResponse;
import com.ecom.analytics.dto.PageResult;
import com.ecom.analytics.dto.ShopRow;
import com.ecom.analytics.dto.UserShopRow;
import com.ecom.analytics.repository.UserShopRepository;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.stereotype.Service;

@Service
public class AccessService {
  private final UserShopRepository userShopRepository;

  public AccessService(UserShopRepository userShopRepository) {
    this.userShopRepository = userShopRepository;
  }

  public GrantAccessResponse grantAccess(GrantAccessRequest request) {
    userShopRepository.addUserToShop(request.userId(), request.shopId());
    return new GrantAccessResponse(request.userId(), request.shopId(), "granted");
  }

  public RevokeAccessResponse revokeAccess(RevokeAccessRequest request) {
    userShopRepository.removeUserFromShop(request.userId(), request.shopId());
    return new RevokeAccessResponse(request.userId(), request.shopId(), "revoked");
  }

  public PageResult<ShopRow> listShopsForUser(long userId, String domainFilter, int limit, int offset, String sortDir) {
    var items = userShopRepository.listShopsForUser(userId, domainFilter, limit, offset, sortDir);
    long total = userShopRepository.countShopsForUser(userId, domainFilter);
    return new PageResult<>(items, total, limit, offset, null);
  }

  public PageResult<UserShopRow> listUsersForShop(long shopId, String emailFilter, int limit, int offset, String sortDir) {
    var items = userShopRepository.listUsersForShop(shopId, emailFilter, limit, offset, sortDir);
    long total = userShopRepository.countUsersForShop(shopId, emailFilter);
    return new PageResult<>(items, total, limit, offset, null);
  }

  public PageResult<UserShopRow> listUsersForShopCursor(long shopId, String emailFilter, int limit, String sortDir, String cursor) {
    java.util.List<UserShopRow> items;
    if (cursor == null || cursor.isBlank()) {
      items = userShopRepository.listUsersForShopFirstPage(shopId, emailFilter, sortDir, limit);
    } else {
      CursorParts parts = decodeCursor(cursor);
      items = userShopRepository.listUsersForShopKeyset(
          shopId, emailFilter, sortDir, parts.value(), parts.id(), limit);
    }
    String nextCursor = buildUserCursor(items);
    return new PageResult<>(items, null, limit, null, nextCursor);
  }

  public PageResult<ShopRow> listShopsForUserCursor(long userId, String domainFilter, int limit, String sortDir, String cursor) {
    java.util.List<ShopRow> items;
    if (cursor == null || cursor.isBlank()) {
      items = userShopRepository.listShopsForUserFirstPage(userId, domainFilter, sortDir, limit);
    } else {
      CursorParts parts = decodeCursor(cursor);
      items = userShopRepository.listShopsForUserKeyset(
          userId, domainFilter, sortDir, parts.value(), parts.id(), limit);
    }
    String nextCursor = buildShopCursor(items);
    return new PageResult<>(items, null, limit, null, nextCursor);
  }

  private String buildUserCursor(java.util.List<UserShopRow> items) {
    if (items.isEmpty()) {
      return null;
    }
    UserShopRow last = items.get(items.size() - 1);
    return encodeCursor(last.email(), last.userId());
  }

  private String buildShopCursor(java.util.List<ShopRow> items) {
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
