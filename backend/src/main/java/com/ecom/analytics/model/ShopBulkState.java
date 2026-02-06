package com.ecom.analytics.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "shop_bulk_state")
public class ShopBulkState {
  @Id
  @Column(name = "shop_id")
  private Long shopId;

  @Column(name = "current_type")
  private String currentType;

  @Column(name = "operation_id")
  private String operationId;

  @Column(name = "status")
  private String status;

  @Column(name = "url")
  private String url;

  @Column(name = "updated_at")
  private Instant updatedAt;

  @PrePersist
  public void prePersist() {
    updatedAt = Instant.now();
  }

  @PreUpdate
  public void preUpdate() {
    updatedAt = Instant.now();
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public String getCurrentType() {
    return currentType;
  }

  public void setCurrentType(String currentType) {
    this.currentType = currentType;
  }

  public String getOperationId() {
    return operationId;
  }

  public void setOperationId(String operationId) {
    this.operationId = operationId;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(Instant updatedAt) {
    this.updatedAt = updatedAt;
  }
}
