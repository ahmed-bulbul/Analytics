package com.ecom.analytics.controller;

import com.ecom.analytics.service.ShopifyClient;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/shopify")
public class ShopifyController {
  private final ShopifyClient client;

  public ShopifyController(ShopifyClient client) {
    this.client = client;
  }

  @GetMapping("/backfill/orders")
  public String backfillOrders(
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
  ) {
    return client.startOrdersBulkOperation(from, to);
  }

  @GetMapping("/backfill/customers")
  public String backfillCustomers(
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
  ) {
    return client.startCustomersBulkOperation(from, to);
  }
}
