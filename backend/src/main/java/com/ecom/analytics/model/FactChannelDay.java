package com.ecom.analytics.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "fact_channel_day")
public class FactChannelDay {
  @EmbeddedId
  private FactChannelDayId id;

  @ManyToOne(fetch = FetchType.LAZY)
  @MapsId("channelId")
  @JoinColumn(name = "channel_id")
  private DimChannel channel;

  @ManyToOne(fetch = FetchType.LAZY)
  @MapsId("shopId")
  @JoinColumn(name = "shop_id")
  private Shop shop;

  @Column(nullable = false)
  private BigDecimal spend;

  @Column(nullable = false)
  private Long impressions;

  @Column(nullable = false)
  private Long clicks;

  @Column(name = "attributed_orders", nullable = false)
  private Integer attributedOrders;

  @Column(name = "attributed_revenue", nullable = false)
  private BigDecimal attributedRevenue;

  public FactChannelDayId getId() {
    return id;
  }

  public void setId(FactChannelDayId id) {
    this.id = id;
  }

  public DimChannel getChannel() {
    return channel;
  }

  public void setChannel(DimChannel channel) {
    this.channel = channel;
  }

  public Shop getShop() {
    return shop;
  }

  public void setShop(Shop shop) {
    this.shop = shop;
  }

  public BigDecimal getSpend() {
    return spend;
  }

  public void setSpend(BigDecimal spend) {
    this.spend = spend;
  }

  public Long getImpressions() {
    return impressions;
  }

  public void setImpressions(Long impressions) {
    this.impressions = impressions;
  }

  public Long getClicks() {
    return clicks;
  }

  public void setClicks(Long clicks) {
    this.clicks = clicks;
  }

  public Integer getAttributedOrders() {
    return attributedOrders;
  }

  public void setAttributedOrders(Integer attributedOrders) {
    this.attributedOrders = attributedOrders;
  }

  public BigDecimal getAttributedRevenue() {
    return attributedRevenue;
  }

  public void setAttributedRevenue(BigDecimal attributedRevenue) {
    this.attributedRevenue = attributedRevenue;
  }
}
