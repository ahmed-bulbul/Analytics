package com.ecom.analytics.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "dim_customers")
public class DimCustomer {
  @EmbeddedId
  private DimCustomerId id;

  @Column
  private String email;

  @Column(name = "email_hash")
  private String emailHash;

  @Column(name = "created_at")
  private Instant createdAt;

  @Column(name = "updated_at")
  private Instant updatedAt;

  @Column(name = "first_order_processed_at")
  private Instant firstOrderProcessedAt;

  @Column(name = "last_order_processed_at")
  private Instant lastOrderProcessedAt;

  public DimCustomerId getId() {
    return id;
  }

  public void setId(DimCustomerId id) {
    this.id = id;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getEmailHash() {
    return emailHash;
  }

  public void setEmailHash(String emailHash) {
    this.emailHash = emailHash;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(Instant updatedAt) {
    this.updatedAt = updatedAt;
  }

  public Instant getFirstOrderProcessedAt() {
    return firstOrderProcessedAt;
  }

  public void setFirstOrderProcessedAt(Instant firstOrderProcessedAt) {
    this.firstOrderProcessedAt = firstOrderProcessedAt;
  }

  public Instant getLastOrderProcessedAt() {
    return lastOrderProcessedAt;
  }

  public void setLastOrderProcessedAt(Instant lastOrderProcessedAt) {
    this.lastOrderProcessedAt = lastOrderProcessedAt;
  }
}
