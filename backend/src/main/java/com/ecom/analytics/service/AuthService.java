package com.ecom.analytics.service;

import com.ecom.analytics.dto.LoginRequest;
import com.ecom.analytics.dto.LoginResponse;
import com.ecom.analytics.dto.RegisterRequest;
import com.ecom.analytics.dto.RegisterResponse;
import com.ecom.analytics.model.AppUser;
import com.ecom.analytics.repository.UserRepository;
import com.ecom.analytics.repository.UserShopRepository;
import com.ecom.analytics.repository.UserWriteRepository;
import java.util.Optional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
  private final UserRepository userRepository;
  private final UserWriteRepository userWriteRepository;
  private final UserShopRepository userShopRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;

  public AuthService(UserRepository userRepository, UserWriteRepository userWriteRepository, UserShopRepository userShopRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
    this.userRepository = userRepository;
    this.userWriteRepository = userWriteRepository;
    this.userShopRepository = userShopRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtService = jwtService;
  }

  public LoginResponse login(LoginRequest request) {
    Optional<AppUser> userOpt = userRepository.findByEmail(request.email());
    if (userOpt.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
    }
    AppUser user = userOpt.get();
    if (!passwordEncoder.matches(request.password(), user.passwordHash())) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
    }

    String token = jwtService.generateToken(user.email(), user.primaryShopId(), user.role(), user.id());
    return new LoginResponse(token, user.primaryShopId(), user.email());
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
    String role = normalizeRole(request.role());
    String hash = passwordEncoder.encode(request.password());
    long id = userWriteRepository.insertUser(request.email().toLowerCase(), hash, request.shopId(), role);
    userShopRepository.addUserToShop(id, request.shopId());
    return new RegisterResponse(id, request.email().toLowerCase(), role, request.shopId());
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
