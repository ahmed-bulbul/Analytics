package com.ecom.analytics.service;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class CryptoService {
  private static final String ALGO = "AES";
  private static final String TRANSFORM = "AES/GCM/NoPadding";
  private static final int IV_LENGTH = 12;
  private static final int TAG_BITS = 128;

  private final SecretKey key;
  private final SecureRandom random = new SecureRandom();

  public CryptoService(@Value("${security.encryption.key-base64}") String base64Key) {
    byte[] keyBytes = Base64.getDecoder().decode(base64Key);
    this.key = new SecretKeySpec(keyBytes, ALGO);
  }

  public EncryptedPayload encrypt(String plaintext) {
    try {
      byte[] iv = new byte[IV_LENGTH];
      random.nextBytes(iv);

      Cipher cipher = Cipher.getInstance(TRANSFORM);
      cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(TAG_BITS, iv));
      byte[] cipherBytes = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

      return new EncryptedPayload(
          Base64.getEncoder().encodeToString(cipherBytes),
          Base64.getEncoder().encodeToString(iv)
      );
    } catch (Exception e) {
      throw new IllegalStateException("Failed to encrypt value", e);
    }
  }

  public String decrypt(String cipherTextBase64, String ivBase64) {
    try {
      byte[] cipherBytes = Base64.getDecoder().decode(cipherTextBase64);
      byte[] iv = Base64.getDecoder().decode(ivBase64);

      Cipher cipher = Cipher.getInstance(TRANSFORM);
      cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(TAG_BITS, iv));
      byte[] plain = cipher.doFinal(cipherBytes);
      return new String(plain, StandardCharsets.UTF_8);
    } catch (Exception e) {
      throw new IllegalStateException("Failed to decrypt value", e);
    }
  }

  public record EncryptedPayload(String cipherTextBase64, String ivBase64) {}
}
