package com.ecom.analytics.service;

import com.ecom.analytics.config.ShopifyConfig;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.TreeMap;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.stereotype.Service;

@Service
public class ShopifyHmacVerifier {
  private final ShopifyConfig config;

  public ShopifyHmacVerifier(ShopifyConfig config) {
    this.config = config;
  }

  public boolean verify(Map<String, String> params) {
    String provided = params.get("hmac");
    if (provided == null || provided.isBlank()) {
      return false;
    }

    Map<String, String> sorted = new TreeMap<>();
    for (var entry : params.entrySet()) {
      String key = entry.getKey();
      if ("hmac".equals(key) || "signature".equals(key)) {
        continue;
      }
      sorted.put(key, entry.getValue());
    }

    StringBuilder message = new StringBuilder();
    for (var entry : sorted.entrySet()) {
      if (message.length() > 0) {
        message.append('&');
      }
      message.append(entry.getKey()).append('=').append(entry.getValue());
    }

    String secret = config.getOauthClientSecret();
    if (secret == null || secret.isBlank()) {
      return false;
    }

    String computed = hmacSha256(message.toString(), secret);
    return constantTimeEquals(computed, provided);
  }

  private String hmacSha256(String data, String secret) {
    try {
      Mac mac = Mac.getInstance("HmacSHA256");
      mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
      byte[] digest = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
      StringBuilder hex = new StringBuilder(digest.length * 2);
      for (byte b : digest) {
        hex.append(String.format("%02x", b));
      }
      return hex.toString();
    } catch (Exception e) {
      throw new IllegalStateException("Failed to compute HMAC", e);
    }
  }

  private boolean constantTimeEquals(String a, String b) {
    if (a == null || b == null || a.length() != b.length()) {
      return false;
    }
    int result = 0;
    for (int i = 0; i < a.length(); i++) {
      result |= a.charAt(i) ^ b.charAt(i);
    }
    return result == 0;
  }
}
