package com.ecom.analytics.repository;

import com.ecom.analytics.dto.ShopRow;
import com.ecom.analytics.dto.UserShopRow;
import com.ecom.analytics.model.UserShop;
import com.ecom.analytics.model.UserShopId;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserShopRepository extends JpaRepository<UserShop, UserShopId> {
  boolean existsByIdUserIdAndIdShopId(Long userId, Long shopId);

  @Query("""
      select new com.ecom.analytics.dto.UserShopRow(u.id, u.email, u.role)
      from UserShop us
      join us.user u
      where us.shop.id = :shopId
        and (:email is null or lower(u.email) like lower(concat('%', :email, '%')))
      order by u.email asc, u.id asc
      """)
  List<UserShopRow> findUsersForShop(@Param("shopId") long shopId, @Param("email") String email, Pageable pageable);

  @Query("""
      select count(u.id)
      from UserShop us
      join us.user u
      where us.shop.id = :shopId
        and (:email is null or lower(u.email) like lower(concat('%', :email, '%')))
      """)
  long countUsersForShop(@Param("shopId") long shopId, @Param("email") String email);

  @Query("""
      select new com.ecom.analytics.dto.ShopRow(s.id, s.shopDomain, s.timezone, s.currency)
      from UserShop us
      join us.shop s
      where us.user.id = :userId
        and (:domain is null or lower(s.shopDomain) like lower(concat('%', :domain, '%')))
      order by s.shopDomain asc, s.id asc
      """)
  List<ShopRow> findShopsForUser(@Param("userId") long userId, @Param("domain") String domain, Pageable pageable);

  @Query("""
      select count(s.id)
      from UserShop us
      join us.shop s
      where us.user.id = :userId
        and (:domain is null or lower(s.shopDomain) like lower(concat('%', :domain, '%')))
      """)
  long countShopsForUser(@Param("userId") long userId, @Param("domain") String domain);

  @Query("""
      select new com.ecom.analytics.dto.UserShopRow(u.id, u.email, u.role)
      from UserShop us
      join us.user u
      where us.shop.id = :shopId
        and (:email is null or lower(u.email) like lower(concat('%', :email, '%')))
        and (lower(u.email) > lower(:cursorEmail)
             or (lower(u.email) = lower(:cursorEmail) and u.id > :cursorId))
      order by u.email asc, u.id asc
      """)
  List<UserShopRow> findUsersForShopCursorAsc(@Param("shopId") long shopId,
                                              @Param("email") String email,
                                              @Param("cursorEmail") String cursorEmail,
                                              @Param("cursorId") long cursorId,
                                              Pageable pageable);

  @Query("""
      select new com.ecom.analytics.dto.UserShopRow(u.id, u.email, u.role)
      from UserShop us
      join us.user u
      where us.shop.id = :shopId
        and (:email is null or lower(u.email) like lower(concat('%', :email, '%')))
        and (lower(u.email) < lower(:cursorEmail)
             or (lower(u.email) = lower(:cursorEmail) and u.id < :cursorId))
      order by u.email desc, u.id desc
      """)
  List<UserShopRow> findUsersForShopCursorDesc(@Param("shopId") long shopId,
                                               @Param("email") String email,
                                               @Param("cursorEmail") String cursorEmail,
                                               @Param("cursorId") long cursorId,
                                               Pageable pageable);

  @Query("""
      select new com.ecom.analytics.dto.ShopRow(s.id, s.shopDomain, s.timezone, s.currency)
      from UserShop us
      join us.shop s
      where us.user.id = :userId
        and (:domain is null or lower(s.shopDomain) like lower(concat('%', :domain, '%')))
        and (lower(s.shopDomain) > lower(:cursorDomain)
             or (lower(s.shopDomain) = lower(:cursorDomain) and s.id > :cursorId))
      order by s.shopDomain asc, s.id asc
      """)
  List<ShopRow> findShopsForUserCursorAsc(@Param("userId") long userId,
                                          @Param("domain") String domain,
                                          @Param("cursorDomain") String cursorDomain,
                                          @Param("cursorId") long cursorId,
                                          Pageable pageable);

  @Query("""
      select new com.ecom.analytics.dto.ShopRow(s.id, s.shopDomain, s.timezone, s.currency)
      from UserShop us
      join us.shop s
      where us.user.id = :userId
        and (:domain is null or lower(s.shopDomain) like lower(concat('%', :domain, '%')))
        and (lower(s.shopDomain) < lower(:cursorDomain)
             or (lower(s.shopDomain) = lower(:cursorDomain) and s.id < :cursorId))
      order by s.shopDomain desc, s.id desc
      """)
  List<ShopRow> findShopsForUserCursorDesc(@Param("userId") long userId,
                                           @Param("domain") String domain,
                                           @Param("cursorDomain") String cursorDomain,
                                           @Param("cursorId") long cursorId,
                                           Pageable pageable);
}
