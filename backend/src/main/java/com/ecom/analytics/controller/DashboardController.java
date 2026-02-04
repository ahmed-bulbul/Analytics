package com.ecom.analytics.controller;

import com.ecom.analytics.dto.ChannelRow;
import com.ecom.analytics.dto.CompareResponse;
import com.ecom.analytics.dto.CohortRow;
import com.ecom.analytics.dto.GrowthResponse;
import com.ecom.analytics.dto.KpiResponse;
import com.ecom.analytics.dto.LtvResponse;
import com.ecom.analytics.service.DashboardService;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
  private final DashboardService service;
  private final com.ecom.analytics.service.AccessService accessService;

  public DashboardController(DashboardService service, com.ecom.analytics.service.AccessService accessService) {
    this.service = service;
    this.accessService = accessService;
  }

  @GetMapping("/kpis")
  public KpiResponse kpis(
      @RequestParam(required = false) Long shopId,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
  ) {
    return service.getKpis(resolveShopId(shopId), from, to);
  }

  @GetMapping("/growth")
  public GrowthResponse growth(
      @RequestParam(required = false) Long shopId,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
  ) {
    return service.getGrowth(resolveShopId(shopId), from, to);
  }

  @GetMapping("/ltv")
  public LtvResponse ltv(@RequestParam(required = false) Long shopId) {
    return service.getLtv(resolveShopId(shopId));
  }

  @GetMapping("/cohorts")
  public List<CohortRow> cohorts(
      @RequestParam(required = false) Long shopId,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
  ) {
    return service.getCohorts(resolveShopId(shopId), from, to);
  }

  @GetMapping("/channels")
  public List<ChannelRow> channels(
      @RequestParam(required = false) Long shopId,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
  ) {
    return service.getChannels(resolveShopId(shopId), from, to);
  }

  @GetMapping("/compare")
  public CompareResponse compare(
      @RequestParam(required = false) Long shopId,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
  ) {
    long days = java.time.temporal.ChronoUnit.DAYS.between(from, to) + 1;
    LocalDate prevTo = from.minusDays(1);
    LocalDate prevFrom = prevTo.minusDays(days - 1);

    long resolvedShopId = resolveShopId(shopId);
    KpiResponse current = service.getKpis(resolvedShopId, from, to);
    KpiResponse previous = service.getKpis(resolvedShopId, prevFrom, prevTo);

    return new CompareResponse(current, previous);
  }

  private long resolveShopId(Long shopId) {
    if (shopId != null) {
      var auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
      if (auth == null || !(auth.getPrincipal() instanceof com.ecom.analytics.security.AuthPrincipal principal)) {
        throw new IllegalArgumentException("shopId is required");
      }
      boolean allowed = accessService.hasAccess(principal.userId(), shopId);
      if (!allowed) {
        throw new IllegalArgumentException("Not authorized for this shop");
      }
      return shopId;
    }
    var auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || !(auth.getPrincipal() instanceof com.ecom.analytics.security.AuthPrincipal principal)) {
      throw new IllegalArgumentException("shopId is required");
    }
    return principal.shopId();
  }
}
