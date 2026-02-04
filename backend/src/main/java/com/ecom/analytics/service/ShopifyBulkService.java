package com.ecom.analytics.service;

import com.ecom.analytics.config.ShopifyConfig;
import java.time.LocalDate;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class ShopifyBulkService {
  private final ShopifyConfig config;

  public ShopifyBulkService(ShopifyConfig config) {
    this.config = config;
  }

  public String startOrdersBulkOperation(String shopDomain, String accessToken, LocalDate from, LocalDate to) {
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

    return postGraphql(shopDomain, accessToken, Map.of("query", query));
  }

  public String startCustomersBulkOperation(String shopDomain, String accessToken, LocalDate from, LocalDate to) {
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

    return postGraphql(shopDomain, accessToken, Map.of("query", query));
  }

  private String postGraphql(String shopDomain, String accessToken, Map<String, Object> payload) {
    RestClient client = RestClient.builder()
        .baseUrl("https://" + shopDomain + "/admin/api/" + config.getApiVersion())
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .build();

    return client.post()
        .uri("/graphql.json")
        .header("X-Shopify-Access-Token", accessToken)
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
        customers(query: "updated_at:>=%s updated_at:<%s", first: 250) {
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
}
