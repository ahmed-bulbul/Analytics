package com.ecom.analytics.model;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

@Embeddable
public class FactChannelDayId implements Serializable {
  private Long shopId;
  private Long channelId;
  private LocalDate date;

  public FactChannelDayId() {}

  public FactChannelDayId(Long shopId, Long channelId, LocalDate date) {
    this.shopId = shopId;
    this.channelId = channelId;
    this.date = date;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public Long getChannelId() {
    return channelId;
  }

  public void setChannelId(Long channelId) {
    this.channelId = channelId;
  }

  public LocalDate getDate() {
    return date;
  }

  public void setDate(LocalDate date) {
    this.date = date;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof FactChannelDayId that)) return false;
    return Objects.equals(shopId, that.shopId) && Objects.equals(channelId, that.channelId) && Objects.equals(date, that.date);
  }

  @Override
  public int hashCode() {
    return Objects.hash(shopId, channelId, date);
  }
}
