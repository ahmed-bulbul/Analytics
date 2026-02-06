package com.ecom.analytics.controller;

import com.ecom.analytics.model.Shop;
import com.ecom.analytics.repository.ShopRepository;
import com.ecom.analytics.service.CryptoService;
import com.ecom.analytics.service.ShopifyBulkService;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/shopify")
public class ShopifyController {
  private final ShopifyBulkService bulkService;
  private final ShopRepository shopRepository;
  private final CryptoService cryptoService;

  public ShopifyController(ShopifyBulkService bulkService, ShopRepository shopRepository, CryptoService cryptoService) {
    this.bulkService = bulkService;
    this.shopRepository = shopRepository;
    this.cryptoService = cryptoService;
  }

  @GetMapping("/backfill/orders")
  public String backfillOrders(
      @RequestParam long shopId,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
  ) {
    Shop shop = shopRepository.findByIdAndDeletedAtIsNull(shopId).orElseThrow();
    String token = cryptoService.decrypt(shop.getShopifyAccessTokenEncrypted(), shop.getShopifyAccessTokenIv());
    return bulkService.startOrdersBulkOperation(shop.getShopDomain(), token, from, to);
  }

  @GetMapping("/backfill/customers")
  public String backfillCustomers(
      @RequestParam long shopId,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
  ) {
    Shop shop = shopRepository.findByIdAndDeletedAtIsNull(shopId).orElseThrow();
    String token = cryptoService.decrypt(shop.getShopifyAccessTokenEncrypted(), shop.getShopifyAccessTokenIv());
    return bulkService.startCustomersBulkOperation(shop.getShopDomain(), token, from, to);
  }
}
