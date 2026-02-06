package com.ecom.analytics.service;

import com.ecom.analytics.dto.SyncStatusResponse;
import com.ecom.analytics.model.Shop;
import com.ecom.analytics.model.ShopBulkState;
import com.ecom.analytics.repository.ShopBulkStateRepository;
import com.ecom.analytics.repository.ShopRepository;
import org.springframework.stereotype.Service;

@Service
public class SyncStatusService {
  private final ShopRepository shopRepository;
  private final ShopBulkStateRepository stateRepository;

  public SyncStatusService(ShopRepository shopRepository, ShopBulkStateRepository stateRepository) {
    this.shopRepository = shopRepository;
    this.stateRepository = stateRepository;
  }

  public SyncStatusResponse getStatus(long shopId) {
    Shop shop = shopRepository.findByIdAndDeletedAtIsNull(shopId).orElseThrow();
    ShopBulkState state = stateRepository.findById(shopId).orElse(null);
    if (state == null) {
      return new SyncStatusResponse(
          shopId,
          "IDLE",
          null,
          null,
          null,
          shop.getLastIncrementalSyncAt()
      );
    }
    return new SyncStatusResponse(
        shopId,
        state.getStatus(),
        state.getCurrentType(),
        state.getOperationId(),
        state.getUpdatedAt(),
        shop.getLastIncrementalSyncAt()
    );
  }
}
