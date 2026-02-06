package com.ecom.analytics.service;

import com.ecom.analytics.dto.LoginRequest;
import com.ecom.analytics.dto.LoginResponse;
import com.ecom.analytics.dto.RegisterRequest;
import com.ecom.analytics.dto.RegisterResponse;
import com.ecom.analytics.model.User;
import com.ecom.analytics.model.UserShop;
import com.ecom.analytics.repository.ShopRepository;
import com.ecom.analytics.repository.UserRepository;
import com.ecom.analytics.repository.UserShopRepository;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {
  private final UserRepository userRepository;
  private final UserShopRepository userShopRepository;
  private final ShopRepository shopRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final AuditService auditService;

  public AuthService(UserRepository userRepository,
                     UserShopRepository userShopRepository,
                     ShopRepository shopRepository,
                     PasswordEncoder passwordEncoder,
                     JwtService jwtService,
                     AuditService auditService) {
    this.userRepository = userRepository;
    this.userShopRepository = userShopRepository;
    this.shopRepository = shopRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtService = jwtService;
    this.auditService = auditService;
  }

  public LoginResponse login(LoginRequest request) {
    Optional<User> userOpt = userRepository.findByEmailIgnoreCaseAndDeletedAtIsNull(request.email());
    if (userOpt.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
    }
    User user = userOpt.get();
    if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
    }
//    if (user.getPrimaryShop() != null && user.getPrimaryShop().getDeletedAt() != null) {
//      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Account disabled");
//    }

    String token = jwtService.generateToken(user.getEmail(), user.getPrimaryShop().getId(), user.getRole(), user.getId());
    return new LoginResponse(token, user.getPrimaryShop().getId(), user.getEmail());
  }

  public RegisterResponse register(RegisterRequest request) {
    if (request.email() == null || request.email().isBlank()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is required");
    }
    if (request.password() == null || request.password().isBlank()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password is required");
    }
    if (request.shopId() == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "shopId is required");
    }
    if (userRepository.findByEmailIgnoreCaseAndDeletedAtIsNull(request.email()).isPresent()) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
    }

    var shop = shopRepository.findByIdAndDeletedAtIsNull(request.shopId())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Shop not found"));

    String role = normalizeRole(request.role());
    String hash = passwordEncoder.encode(request.password());

    User user = new User();
    user.setEmail(request.email().toLowerCase());
    user.setPasswordHash(hash);
    user.setPrimaryShop(shop);
    user.setRole(role);
    user = userRepository.save(user);

    userShopRepository.save(new UserShop(user, shop));

    auditService.record("USER_CREATED", user.getId(), shop.getId(), java.util.Map.of(
        "email", user.getEmail(),
        "role", role
    ));

    return new RegisterResponse(user.getId(), user.getEmail(), role, shop.getId());
  }

  private String normalizeRole(String role) {
    if (role == null || role.isBlank()) {
      return "VIEWER";
    }
    String normalized = role.trim().toUpperCase();
    if (!normalized.equals("ADMIN") && !normalized.equals("VIEWER")) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "role must be ADMIN or VIEWER");
    }
    return normalized;
  }
}
