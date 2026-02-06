package com.ecom.analytics.service;

import com.ecom.analytics.model.DimCustomer;
import com.ecom.analytics.model.DimCustomerId;
import com.ecom.analytics.model.FactOrder;
import com.ecom.analytics.model.FactOrderId;
import com.ecom.analytics.repository.DimCustomerRepository;
import com.ecom.analytics.repository.FactOrderRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ShopifyBulkProcessor {
  private static final Logger log = LoggerFactory.getLogger(ShopifyBulkProcessor.class);
  private final ObjectMapper mapper = new ObjectMapper();
  private final FactOrderRepository factOrderRepository;
  private final DimCustomerRepository dimCustomerRepository;

  public ShopifyBulkProcessor(FactOrderRepository factOrderRepository, DimCustomerRepository dimCustomerRepository) {
    this.factOrderRepository = factOrderRepository;
    this.dimCustomerRepository = dimCustomerRepository;
  }

  public int processOrders(long shopId, String url) throws Exception {
    int count = 0;
    List<FactOrder> batch = new ArrayList<>();
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(url).openStream(), StandardCharsets.UTF_8))) {
      String line;
      while ((line = reader.readLine()) != null) {
        if (line.isBlank()) continue;
        JsonNode node = mapper.readTree(line);
        if (node.get("id") == null) continue;

        FactOrder order = new FactOrder();
        order.setId(new FactOrderId(shopId, node.get("id").asText()));
        order.setOrderName(text(node, "name"));
        order.setCreatedAt(instant(node, "createdAt"));
        order.setProcessedAt(instant(node, "processedAt"));
        order.setUpdatedAt(instant(node, "updatedAt"));
        order.setCancelledAt(instant(node, "cancelledAt"));
        order.setFinancialStatus(text(node, "financialStatus"));
        order.setCustomerGid(node.path("customer").path("id").isMissingNode() ? null : node.path("customer").path("id").asText());

        order.setGrossTotal(money(node, "totalPriceSet"));
        order.setGrossTax(money(node, "totalTaxSet"));
        order.setGrossShipping(money(node, "totalShippingPriceSet"));
        order.setNetSales(money(node, "currentSubtotalPriceSet"));

        batch.add(order);
        count++;

        if (batch.size() >= 500) {
          factOrderRepository.saveAll(batch);
          batch.clear();
        }
      }
    }
    if (!batch.isEmpty()) {
      factOrderRepository.saveAll(batch);
    }
    log.info("Processed {} orders", count);
    return count;
  }

  public int processCustomers(long shopId, String url) throws Exception {
    int count = 0;
    List<DimCustomer> batch = new ArrayList<>();
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(url).openStream(), StandardCharsets.UTF_8))) {
      String line;
      while ((line = reader.readLine()) != null) {
        if (line.isBlank()) continue;
        JsonNode node = mapper.readTree(line);
        if (node.get("id") == null) continue;

        DimCustomer c = new DimCustomer();
        c.setId(new DimCustomerId(shopId, node.get("id").asText()));
        c.setEmail(text(node, "email"));
        c.setCreatedAt(instant(node, "createdAt"));
        c.setUpdatedAt(instant(node, "updatedAt"));
        batch.add(c);
        count++;

        if (batch.size() >= 500) {
          dimCustomerRepository.saveAll(batch);
          batch.clear();
        }
      }
    }
    if (!batch.isEmpty()) {
      dimCustomerRepository.saveAll(batch);
    }
    log.info("Processed {} customers", count);
    return count;
  }

  public int processOrdersWithRetry(long shopId, String url) throws Exception {
    return runWithRetry("orders", () -> processOrders(shopId, url));
  }

  public int processCustomersWithRetry(long shopId, String url) throws Exception {
    return runWithRetry("customers", () -> processCustomers(shopId, url));
  }

  private int runWithRetry(String label, Callable<Integer> action) throws Exception {
    int attempt = 0;
    long delayMs = 1000;
    while (true) {
      try {
        return action.call();
      } catch (Exception ex) {
        attempt++;
        if (attempt >= 3) {
          throw ex;
        }
        log.warn("Bulk processing for {} failed on attempt {}. Retrying in {}ms", label, attempt, delayMs, ex);
        Thread.sleep(delayMs);
        delayMs *= 2;
      }
    }
  }

  private String text(JsonNode node, String field) {
    JsonNode n = node.get(field);
    return n == null || n.isNull() ? null : n.asText();
  }

  private Instant instant(JsonNode node, String field) {
    String value = text(node, field);
    return value == null ? null : Instant.parse(value);
  }

  private BigDecimal money(JsonNode node, String field) {
    JsonNode amountNode = node.path(field).path("shopMoney").path("amount");
    if (amountNode.isMissingNode() || amountNode.isNull()) {
      return BigDecimal.ZERO;
    }
    return new BigDecimal(amountNode.asText());
  }
}
