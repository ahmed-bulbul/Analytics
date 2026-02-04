package com.ecom.analytics.service;

import com.ecom.analytics.dto.ChannelRow;
import com.ecom.analytics.dto.CohortRow;
import com.ecom.analytics.dto.GrowthResponse;
import com.ecom.analytics.dto.KpiResponse;
import com.ecom.analytics.dto.LtvResponse;
import com.ecom.analytics.repository.DashboardRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {
  private final DashboardRepository repository;

  public DashboardService(DashboardRepository repository) {
    this.repository = repository;
  }

  public KpiResponse getKpis(long shopId, LocalDate from, LocalDate to) {
    return repository.fetchKpis(shopId, from, to);
  }

  public GrowthResponse getGrowth(long shopId, LocalDate from, LocalDate to) {
    return repository.fetchGrowth(shopId, from, to);
  }

  public LtvResponse getLtv(long shopId) {
    return repository.fetchLtv(shopId);
  }

  public List<CohortRow> getCohorts(long shopId, LocalDate from, LocalDate to) {
    return repository.fetchCohorts(shopId, from, to);
  }

  public List<ChannelRow> getChannels(long shopId, LocalDate from, LocalDate to) {
    return repository.fetchChannels(shopId, from, to);
  }
}
