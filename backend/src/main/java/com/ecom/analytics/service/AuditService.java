package com.ecom.analytics.service;

import com.ecom.analytics.model.AuditEvent;
import com.ecom.analytics.repository.AuditEventRepository;
import com.ecom.analytics.security.AuthPrincipal;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuditService {
  private final AuditEventRepository auditEventRepository;
  private final ObjectMapper objectMapper = new ObjectMapper();

  public AuditService(AuditEventRepository auditEventRepository) {
    this.auditEventRepository = auditEventRepository;
  }

  public void record(String action, Long targetUserId, Long targetShopId, Map<String, Object> metadata) {
    AuthPrincipal actor = currentPrincipal();
    String actorEmail = actor == null ? null : actor.email();
    Long actorUserId = actor == null ? null : actor.userId();
    saveEvent(action, actorUserId, actorEmail, targetUserId, targetShopId, metadata);
  }

  public void recordAs(String action, Long actorUserId, String actorEmail, Long targetUserId, Long targetShopId, Map<String, Object> metadata) {
    saveEvent(action, actorUserId, actorEmail, targetUserId, targetShopId, metadata);
  }

  private void saveEvent(String action, Long actorUserId, String actorEmail, Long targetUserId, Long targetShopId, Map<String, Object> metadata) {
    HttpServletRequest request = RequestContext.currentRequest();
    AuditEvent event = new AuditEvent();
    event.setAction(action);
    event.setActorUserId(actorUserId);
    event.setActorEmail(actorEmail);
    event.setTargetUserId(targetUserId);
    event.setTargetShopId(targetShopId);
    event.setMetadata(serialize(metadata));
    event.setIpAddress(RequestContext.clientIp(request));
    event.setUserAgent(request == null ? null : request.getHeader("User-Agent"));
    auditEventRepository.save(event);
  }

  private String serialize(Map<String, Object> metadata) {
    if (metadata == null || metadata.isEmpty()) {
      return null;
    }
    try {
      return objectMapper.writeValueAsString(metadata);
    } catch (JsonProcessingException e) {
      return metadata.toString();
    }
  }

  private AuthPrincipal currentPrincipal() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth != null && auth.getPrincipal() instanceof AuthPrincipal principal) {
      return principal;
    }
    return null;
  }
}
