package com.ecom.analytics.controller;

import com.ecom.analytics.dto.SyncStatusResponse;
import com.ecom.analytics.service.SyncStatusService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sync")
public class SyncStatusController {
  private final SyncStatusService service;

  public SyncStatusController(SyncStatusService service) {
    this.service = service;
  }

  @GetMapping("/status")
  public SyncStatusResponse status(@RequestParam long shopId) {
    return service.getStatus(shopId);
  }
}
