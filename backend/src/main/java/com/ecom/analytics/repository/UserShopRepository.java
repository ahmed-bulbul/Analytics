package com.ecom.analytics.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UserShopRepository {
  private final JdbcTemplate jdbcTemplate;

  public UserShopRepository(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  public void addUserToShop(long userId, long shopId) {
    String sql = "INSERT INTO user_shops (user_id, shop_id) VALUES (?, ?)";
    jdbcTemplate.update(sql, userId, shopId);
  }

  public void removeUserFromShop(long userId, long shopId) {
    String sql = "DELETE FROM user_shops WHERE user_id = ? AND shop_id = ?";
    jdbcTemplate.update(sql, userId, shopId);
  }

  public java.util.List<com.ecom.analytics.dto.ShopRow> listShopsForUser(long userId, String domainFilter, int limit, int offset, String sortDir) {
    String sql = """
        SELECT s.shop_id, s.shop_domain, s.timezone, s.currency
        FROM user_shops us
        JOIN shops s ON s.shop_id = us.shop_id
        WHERE us.user_id = ?
          AND (? IS NULL OR LOWER(s.shop_domain) LIKE ?)
        ORDER BY s.shop_domain %s, s.shop_id %s
        LIMIT ? OFFSET ?
        """.formatted(sortDir, sortDir);
    String like = domainFilter == null ? null : "%" + domainFilter.toLowerCase() + "%";
    return jdbcTemplate.query(sql, (rs, rowNum) -> new com.ecom.analytics.dto.ShopRow(
        rs.getLong("shop_id"),
        rs.getString("shop_domain"),
        rs.getString("timezone"),
        rs.getString("currency")
    ), userId, domainFilter == null ? null : domainFilter, like, limit, offset);
  }

  public java.util.List<com.ecom.analytics.dto.UserShopRow> listUsersForShop(long shopId, String emailFilter, int limit, int offset, String sortDir) {
    String sql = """
        SELECT u.id, u.email, u.role
        FROM user_shops us
        JOIN users u ON u.id = us.user_id
        WHERE us.shop_id = ?
          AND (? IS NULL OR LOWER(u.email) LIKE ?)
        ORDER BY u.email %s, u.id %s
        LIMIT ? OFFSET ?
        """.formatted(sortDir, sortDir);
    String like = emailFilter == null ? null : "%" + emailFilter.toLowerCase() + "%";
    return jdbcTemplate.query(sql, (rs, rowNum) -> new com.ecom.analytics.dto.UserShopRow(
        rs.getLong("id"),
        rs.getString("email"),
        rs.getString("role")
    ), shopId, emailFilter == null ? null : emailFilter, like, limit, offset);
  }

  public java.util.List<com.ecom.analytics.dto.UserShopRow> listUsersForShopKeyset(long shopId, String emailFilter, String sortDir, String cursorEmail, long cursorId, int limit) {
    String comparator = sortDir.equalsIgnoreCase("DESC") ? "<" : ">";
    String sql = """
        SELECT u.id, u.email, u.role
        FROM user_shops us
        JOIN users u ON u.id = us.user_id
        WHERE us.shop_id = ?
          AND (? IS NULL OR LOWER(u.email) LIKE ?)
          AND (
            LOWER(u.email) %s LOWER(?)
            OR (LOWER(u.email) = LOWER(?) AND u.id %s ?)
          )
        ORDER BY u.email %s, u.id %s
        LIMIT ?
        """.formatted(comparator, comparator, sortDir, sortDir);
    String like = emailFilter == null ? null : "%" + emailFilter.toLowerCase() + "%";
    return jdbcTemplate.query(sql, (rs, rowNum) -> new com.ecom.analytics.dto.UserShopRow(
        rs.getLong("id"),
        rs.getString("email"),
        rs.getString("role")
    ), shopId, emailFilter == null ? null : emailFilter, like, cursorEmail, cursorEmail, cursorId, limit);
  }

  public java.util.List<com.ecom.analytics.dto.UserShopRow> listUsersForShopFirstPage(long shopId, String emailFilter, String sortDir, int limit) {
    String sql = """
        SELECT u.id, u.email, u.role
        FROM user_shops us
        JOIN users u ON u.id = us.user_id
        WHERE us.shop_id = ?
          AND (? IS NULL OR LOWER(u.email) LIKE ?)
        ORDER BY u.email %s, u.id %s
        LIMIT ?
        """.formatted(sortDir, sortDir);
    String like = emailFilter == null ? null : "%" + emailFilter.toLowerCase() + "%";
    return jdbcTemplate.query(sql, (rs, rowNum) -> new com.ecom.analytics.dto.UserShopRow(
        rs.getLong("id"),
        rs.getString("email"),
        rs.getString("role")
    ), shopId, emailFilter == null ? null : emailFilter, like, limit);
  }

  public java.util.List<com.ecom.analytics.dto.ShopRow> listShopsForUserKeyset(long userId, String domainFilter, String sortDir, String cursorDomain, long cursorShopId, int limit) {
    String comparator = sortDir.equalsIgnoreCase("DESC") ? "<" : ">";
    String sql = """
        SELECT s.shop_id, s.shop_domain, s.timezone, s.currency
        FROM user_shops us
        JOIN shops s ON s.shop_id = us.shop_id
        WHERE us.user_id = ?
          AND (? IS NULL OR LOWER(s.shop_domain) LIKE ?)
          AND (
            LOWER(s.shop_domain) %s LOWER(?)
            OR (LOWER(s.shop_domain) = LOWER(?) AND s.shop_id %s ?)
          )
        ORDER BY s.shop_domain %s, s.shop_id %s
        LIMIT ?
        """.formatted(comparator, comparator, sortDir, sortDir);
    String like = domainFilter == null ? null : "%" + domainFilter.toLowerCase() + "%";
    return jdbcTemplate.query(sql, (rs, rowNum) -> new com.ecom.analytics.dto.ShopRow(
        rs.getLong("shop_id"),
        rs.getString("shop_domain"),
        rs.getString("timezone"),
        rs.getString("currency")
    ), userId, domainFilter == null ? null : domainFilter, like, cursorDomain, cursorDomain, cursorShopId, limit);
  }

  public java.util.List<com.ecom.analytics.dto.ShopRow> listShopsForUserFirstPage(long userId, String domainFilter, String sortDir, int limit) {
    String sql = """
        SELECT s.shop_id, s.shop_domain, s.timezone, s.currency
        FROM user_shops us
        JOIN shops s ON s.shop_id = us.shop_id
        WHERE us.user_id = ?
          AND (? IS NULL OR LOWER(s.shop_domain) LIKE ?)
        ORDER BY s.shop_domain %s, s.shop_id %s
        LIMIT ?
        """.formatted(sortDir, sortDir);
    String like = domainFilter == null ? null : "%" + domainFilter.toLowerCase() + "%";
    return jdbcTemplate.query(sql, (rs, rowNum) -> new com.ecom.analytics.dto.ShopRow(
        rs.getLong("shop_id"),
        rs.getString("shop_domain"),
        rs.getString("timezone"),
        rs.getString("currency")
    ), userId, domainFilter == null ? null : domainFilter, like, limit);
  }

  public long countShopsForUser(long userId, String domainFilter) {
    String sql = """
        SELECT COUNT(*)
        FROM user_shops us
        JOIN shops s ON s.shop_id = us.shop_id
        WHERE us.user_id = ?
          AND (? IS NULL OR LOWER(s.shop_domain) LIKE ?)
        """;
    String like = domainFilter == null ? null : "%" + domainFilter.toLowerCase() + "%";
    Long count = jdbcTemplate.queryForObject(sql, Long.class, userId, domainFilter == null ? null : domainFilter, like);
    return count == null ? 0 : count;
  }

  public long countUsersForShop(long shopId, String emailFilter) {
    String sql = """
        SELECT COUNT(*)
        FROM user_shops us
        JOIN users u ON u.id = us.user_id
        WHERE us.shop_id = ?
          AND (? IS NULL OR LOWER(u.email) LIKE ?)
        """;
    String like = emailFilter == null ? null : "%" + emailFilter.toLowerCase() + "%";
    Long count = jdbcTemplate.queryForObject(sql, Long.class, shopId, emailFilter == null ? null : emailFilter, like);
    return count == null ? 0 : count;
  }

  public boolean hasAccess(long userId, long shopId) {
    String sql = "SELECT COUNT(*) FROM user_shops WHERE user_id = ? AND shop_id = ?";
    Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userId, shopId);
    return count != null && count > 0;
  }
}
