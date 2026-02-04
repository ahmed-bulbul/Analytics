package com.ecom.analytics.repository;

import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class UserWriteRepository {
  private final JdbcTemplate jdbcTemplate;

  public UserWriteRepository(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  public long insertUser(String email, String passwordHash, long primaryShopId, String role) {
    String sql = """
        INSERT INTO users (email, password_hash, primary_shop_id, role)
        VALUES (?, ?, ?, ?)
        """;

    KeyHolder keyHolder = new GeneratedKeyHolder();
    jdbcTemplate.update(connection -> {
      var ps = connection.prepareStatement(sql, new String[] {"id"});
      ps.setString(1, email);
      ps.setString(2, passwordHash);
      ps.setLong(3, primaryShopId);
      ps.setString(4, role);
      return ps;
    }, keyHolder);

    Map<String, Object> keys = keyHolder.getKeys();
    if (keys != null && keys.containsKey("ID")) {
      return ((Number) keys.get("ID")).longValue();
    }
    if (keyHolder.getKey() != null) {
      return keyHolder.getKey().longValue();
    }
    throw new IllegalStateException("Failed to create user");
  }
}
