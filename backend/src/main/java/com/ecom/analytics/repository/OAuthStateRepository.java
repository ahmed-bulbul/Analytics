package com.ecom.analytics.repository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class OAuthStateRepository {
  private final JdbcTemplate jdbcTemplate;

  public OAuthStateRepository(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  public void saveState(long shopId, String state) {
    String deleteSql = "DELETE FROM shop_oauth_state WHERE shop_id = ?";
    jdbcTemplate.update(deleteSql, shopId);

    String insertSql = """
        INSERT INTO shop_oauth_state (shop_id, state, created_at)
        VALUES (?, ?, ?)
        """;
    jdbcTemplate.update(insertSql, shopId, state, Timestamp.from(Instant.now()));
  }

  public Optional<String> findState(long shopId) {
    String sql = "SELECT state FROM shop_oauth_state WHERE shop_id = ?";
    return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getString("state"), shopId).stream().findFirst();
  }

  public Optional<Long> findShopIdByState(String state) {
    String sql = "SELECT shop_id FROM shop_oauth_state WHERE state = ?";
    return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getLong("shop_id"), state).stream().findFirst();
  }

  public void deleteState(long shopId) {
    jdbcTemplate.update("DELETE FROM shop_oauth_state WHERE shop_id = ?", shopId);
  }
}
