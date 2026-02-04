package com.ecom.analytics.repository;

import com.ecom.analytics.model.ShopOAuthState;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShopOAuthStateRepository extends JpaRepository<ShopOAuthState, Long> {
  Optional<ShopOAuthState> findByState(String state);
}
