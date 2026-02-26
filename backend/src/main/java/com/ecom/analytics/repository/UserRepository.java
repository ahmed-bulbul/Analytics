package com.ecom.analytics.repository;

import com.ecom.analytics.model.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByEmailIgnoreCaseAndDeletedAtIsNull(String email);
  boolean existsByEmailIgnoreCaseAndDeletedAtIsNull(String email);
  Optional<User> findByIdAndDeletedAtIsNull(Long id);
  Optional<User> findById(Long id);
}
