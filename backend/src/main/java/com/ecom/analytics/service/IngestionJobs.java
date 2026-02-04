package com.ecom.analytics.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class IngestionJobs {

  @Scheduled(cron = "0 0 */4 * * *")
  public void incrementalSync() {
    // Placeholder for Shopify + channel incremental sync.
    // Implement Shopify GraphQL bulk pull + channel API pulls,
    // then write to fact tables and recompute aggregates.
  }
}
