package com.ecom.analytics.service;

import com.ecom.analytics.config.ShopifyConfig;
import java.time.LocalDate;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class ShopifyClient {
  private final ShopifyConfig config;
  private final RestClient restClient;

  public ShopifyClient(ShopifyConfig config) {
    this.config = config;
    this.restClient = RestClient.builder()
        .baseUrl(config.getBaseUrl() + "/admin/api/" + config.getApiVersion())
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .build();
  }

  public String startOrdersBulkOperation(LocalDate from, LocalDate to) {
    String token = requireToken();

    String query = """
      mutation {
        bulkOperationRunQuery(
          query: \"\"\"%s\"\"\"
        ) {
          bulkOperation { id status }
          userErrors { field message }
        }
      }
      """.formatted(buildOrdersQuery(from, to));

    Map<String, Object> payload = Map.of("query", query);

    return restClient.post()
        .uri("/graphql.json")
        .header("X-Shopify-Access-Token", token)
        .body(payload)
        .retrieve()
        .body(String.class);
  }

  public String startCustomersBulkOperation(LocalDate from, LocalDate to) {
    String token = requireToken();

    String query = """
      mutation {
        bulkOperationRunQuery(
          query: \"\"\"%s\"\"\"
        ) {
          bulkOperation { id status }
          userErrors { field message }
        }
      }
      """.formatted(buildCustomersQuery(from, to));

    Map<String, Object> payload = Map.of("query", query);

    return restClient.post()
        .uri("/graphql.json")
        .header("X-Shopify-Access-Token", token)
        .body(payload)
        .retrieve()
        .body(String.class);
  }

  private String buildOrdersQuery(LocalDate from, LocalDate to) {
    return """
      {
        orders(query: "processed_at:>=%s processed_at:<%s", first: 250) {
          edges {
            node {
              id
              name
              createdAt
              processedAt
              updatedAt
              cancelledAt
              financialStatus
              totalPriceSet { shopMoney { amount currencyCode } }
              totalTaxSet { shopMoney { amount currencyCode } }
              totalShippingPriceSet { shopMoney { amount currencyCode } }
              currentSubtotalPriceSet { shopMoney { amount currencyCode } }
              customer { id email }
            }
          }
        }
      }
      """.formatted(from, to.plusDays(1));
  }

  private String buildCustomersQuery(LocalDate from, LocalDate to) {
    return """
      {
        customers(query: "created_at:>=%s created_at:<%s", first: 250) {
          edges {
            node {
              id
              email
              createdAt
              updatedAt
            }
          }
        }
      }
      """.formatted(from, to.plusDays(1));
  }

  private String requireToken() {
    String token = config.getAccessToken();
    if (token == null || token.isBlank()) {
      throw new IllegalStateException("Shopify access token is not configured. Set shopify.access-token in application.yml");
    }
    return token;
  }
}
