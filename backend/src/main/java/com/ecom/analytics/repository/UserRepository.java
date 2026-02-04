package com.ecom.analytics.repository;

import com.ecom.analytics.model.AppUser;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository {
  private final JdbcTemplate jdbcTemplate;

  public UserRepository(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  public Optional<AppUser> findByEmail(String email) {
    String sql = """
        SELECT id, email, password_hash, primary_shop_id, role
        FROM users
        WHERE email = ?
        """;

    return jdbcTemplate.query(sql, userMapper(), email).stream().findFirst();
  }

  private RowMapper<AppUser> userMapper() {
    return (ResultSet rs, int rowNum) -> new AppUser(
        rs.getLong("id"),
        rs.getString("email"),
        rs.getString("password_hash"),
        rs.getLong("primary_shop_id"),
        rs.getString("role")
    );
  }
}
