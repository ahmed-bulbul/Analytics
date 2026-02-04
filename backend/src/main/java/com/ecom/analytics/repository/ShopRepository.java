package com.ecom.analytics.repository;

import com.ecom.analytics.service.CryptoService;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class ShopRepository {
  private final JdbcTemplate jdbcTemplate;
  private final CryptoService cryptoService;

  public ShopRepository(JdbcTemplate jdbcTemplate, CryptoService cryptoService) {
    this.jdbcTemplate = jdbcTemplate;
    this.cryptoService = cryptoService;
  }

  public long createShop(String shopDomain) {
    String sql = """
        INSERT INTO shops (shop_domain, currency, timezone)
        VALUES (?, 'USD', 'UTC')
        """;

    KeyHolder keyHolder = new GeneratedKeyHolder();
    jdbcTemplate.update(connection -> {
      var ps = connection.prepareStatement(sql, new String[] {"shop_id"});
      ps.setString(1, shopDomain);
      return ps;
    }, keyHolder);

    Map<String, Object> keys = keyHolder.getKeys();
    if (keys != null && keys.containsKey("SHOP_ID")) {
      return ((Number) keys.get("SHOP_ID")).longValue();
    }
    if (keyHolder.getKey() != null) {
      return keyHolder.getKey().longValue();
    }
    throw new IllegalStateException("Failed to create shop");
  }

  public String findShopDomain(long shopId) {
    String sql = "SELECT shop_domain FROM shops WHERE shop_id = ?";
    return jdbcTemplate.queryForObject(sql, String.class, shopId);
  }

  public boolean shopDomainExists(String shopDomain) {
    String sql = "SELECT COUNT(*) FROM shops WHERE LOWER(shop_domain) = LOWER(?)";
    Integer count = jdbcTemplate.queryForObject(sql, Integer.class, shopDomain);
    return count != null && count > 0;
  }

  public void storeShopifyToken(long shopId, String accessToken, String scopes) {
    CryptoService.EncryptedPayload encrypted = cryptoService.encrypt(accessToken);
    String sql = """
        UPDATE shops
        SET shopify_access_token_encrypted = ?, shopify_access_token_iv = ?, shopify_scopes = ?, shopify_installed_at = CURRENT_TIMESTAMP
        WHERE shop_id = ?
        """;
    jdbcTemplate.update(sql, encrypted.cipherTextBase64(), encrypted.ivBase64(), scopes, shopId);
  }
}
