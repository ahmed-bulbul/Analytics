package com.ecom.analytics.model;

import jakarta.persistence.*;

@Entity
@Table(name = "user_shops")
public class UserShop {
  @EmbeddedId
  private UserShopId id;

  @ManyToOne(fetch = FetchType.LAZY)
  @MapsId("userId")
  @JoinColumn(name = "user_id")
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @MapsId("shopId")
  @JoinColumn(name = "shop_id")
  private Shop shop;

  public UserShop() {}

  public UserShop(User user, Shop shop) {
    this.user = user;
    this.shop = shop;
    this.id = new UserShopId(user.getId(), shop.getId());
  }

  public UserShopId getId() {
    return id;
  }

  public void setId(UserShopId id) {
    this.id = id;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public Shop getShop() {
    return shop;
  }

  public void setShop(Shop shop) {
    this.shop = shop;
  }
}
