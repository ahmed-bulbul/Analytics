package com.ecom.analytics.model;

import jakarta.persistence.*;

@Entity
@Table(name = "dim_channel")
public class DimChannel {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "channel_id")
  private Long id;

  @Column(name = "channel_key", nullable = false, unique = true)
  private String channelKey;

  @Column(name = "channel_name", nullable = false)
  private String channelName;

  @Column(name = "channel_type", nullable = false)
  private String channelType;

  @Column(nullable = false)
  private String status;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getChannelKey() {
    return channelKey;
  }

  public void setChannelKey(String channelKey) {
    this.channelKey = channelKey;
  }

  public String getChannelName() {
    return channelName;
  }

  public void setChannelName(String channelName) {
    this.channelName = channelName;
  }

  public String getChannelType() {
    return channelType;
  }

  public void setChannelType(String channelType) {
    this.channelType = channelType;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }
}
