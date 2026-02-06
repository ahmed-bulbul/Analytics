package com.ecom.analytics.service;

import com.ecom.analytics.model.Shop;
import com.ecom.analytics.model.ShopBulkState;
import com.ecom.analytics.repository.ShopBulkStateRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ShopifyBulkCoordinator {
  private static final Logger log = LoggerFactory.getLogger(ShopifyBulkCoordinator.class);
  private final ShopifyBulkService bulkService;
  private final ShopifyBulkProcessor processor;
  private final ShopBulkStateRepository stateRepository;
  private final ObjectMapper mapper = new ObjectMapper();

  public ShopifyBulkCoordinator(ShopifyBulkService bulkService,
                                ShopifyBulkProcessor processor,
                                ShopBulkStateRepository stateRepository) {
    this.bulkService = bulkService;
    this.processor = processor;
    this.stateRepository = stateRepository;
  }

  public void tick(Shop shop, String token, LocalDate from, LocalDate to) {
    ShopBulkState state = stateRepository.findById(shop.getId()).orElseGet(() -> {
      ShopBulkState s = new ShopBulkState();
      s.setShopId(shop.getId());
      s.setCurrentType("ORDERS");
      return s;
    });

    BulkInfo current = fetchCurrent(shop.getShopDomain(), token);
    if (current == null) {
      return;
    }

    if ("COMPLETED".equalsIgnoreCase(current.status()) && current.url() != null) {
      try {
        if ("ORDERS".equalsIgnoreCase(state.getCurrentType())) {
          processor.processOrdersWithRetry(shop.getId(), current.url());
          state.setCurrentType("CUSTOMERS");
        } else {
          processor.processCustomersWithRetry(shop.getId(), current.url());
          state.setCurrentType("ORDERS");
        }
        state.setStatus("PROCESSED");
        state.setUrl(null);
        stateRepository.save(state);
      } catch (Exception ex) {
        log.error("Failed to process bulk results for shop {}", shop.getShopDomain(), ex);
      }
      return;
    }

    if ("CREATED".equalsIgnoreCase(current.status()) || "RUNNING".equalsIgnoreCase(current.status())) {
      log.info("Bulk operation in progress for shop {}", shop.getShopDomain());
      return;
    }

    // Start next bulk op
    String resp;
    if ("ORDERS".equalsIgnoreCase(state.getCurrentType())) {
      resp = bulkService.startOrdersBulkOperation(shop.getShopDomain(), token, from, to);
    } else {
      resp = bulkService.startCustomersBulkOperation(shop.getShopDomain(), token, from, to);
    }
    String opId = extractOperationId(resp);
    state.setOperationId(opId);
    state.setStatus("CREATED");
    stateRepository.save(state);
  }

  private BulkInfo fetchCurrent(String shopDomain, String token) {
    try {
      String json = bulkService.currentBulkOperation(shopDomain, token);
      JsonNode node = mapper.readTree(json).path("data").path("currentBulkOperation");
      if (node.isMissingNode() || node.isNull()) {
        return null;
      }
      return new BulkInfo(
          text(node, "id"),
          text(node, "status"),
          text(node, "url")
      );
    } catch (Exception e) {
      return null;
    }
  }

  private String extractOperationId(String json) {
    try {
      JsonNode node = mapper.readTree(json)
          .path("data")
          .path("bulkOperationRunQuery")
          .path("bulkOperation")
          .path("id");
      return node.isMissingNode() ? null : node.asText();
    } catch (Exception e) {
      return null;
    }
  }

  private String text(JsonNode node, String field) {
    JsonNode n = node.get(field);
    return n == null || n.isNull() ? null : n.asText();
  }

  private record BulkInfo(String id, String status, String url) {}
}
