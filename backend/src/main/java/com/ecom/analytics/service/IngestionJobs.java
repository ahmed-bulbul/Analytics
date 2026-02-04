package com.ecom.analytics.service;

import com.ecom.analytics.model.Shop;
import com.ecom.analytics.repository.ShopRepository;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class IngestionJobs {
  private static final Logger log = LoggerFactory.getLogger(IngestionJobs.class);

  private final ShopRepository shopRepository;
  private final ShopifyBulkService bulkService;
  private final CryptoService cryptoService;

  public IngestionJobs(ShopRepository shopRepository, ShopifyBulkService bulkService, CryptoService cryptoService) {
    this.shopRepository = shopRepository;
    this.bulkService = bulkService;
    this.cryptoService = cryptoService;
  }

  @Scheduled(cron = "0 0 */4 * * *")
  @Transactional
  public void incrementalSync() {
    List<Shop> shops = shopRepository.findByShopifyAccessTokenEncryptedIsNotNull();
    if (shops.isEmpty()) {
      log.info("No shops with Shopify tokens found for incremental sync");
      return;
    }

    Instant now = Instant.now();
    for (Shop shop : shops) {
      try {
        String token = cryptoService.decrypt(shop.getShopifyAccessTokenEncrypted(), shop.getShopifyAccessTokenIv());
        Instant lastSync = shop.getLastIncrementalSyncAt();
        Instant fromInstant = lastSync == null ? now.minus(30, ChronoUnit.DAYS) : lastSync.minus(30, ChronoUnit.DAYS);

        ZoneId zone = zoneForShop(shop);
        LocalDate from = fromInstant.atZone(zone).toLocalDate();
        LocalDate to = now.atZone(zone).toLocalDate();

        log.info("Starting incremental sync for shop {} from {} to {}", shop.getShopDomain(), from, to);
        bulkService.startOrdersBulkOperation(shop.getShopDomain(), token, from, to);
        bulkService.startCustomersBulkOperation(shop.getShopDomain(), token, from, to);

        shop.setLastIncrementalSyncAt(now);
        shopRepository.save(shop);
      } catch (Exception ex) {
        log.error("Incremental sync failed for shop {}", shop.getShopDomain(), ex);
      }
    }
  }

  private ZoneId zoneForShop(Shop shop) {
    try {
      if (shop.getTimezone() != null && !shop.getTimezone().isBlank()) {
        return ZoneId.of(shop.getTimezone());
      }
    } catch (Exception ignored) {
    }
    return ZoneId.of("UTC");
  }
}
