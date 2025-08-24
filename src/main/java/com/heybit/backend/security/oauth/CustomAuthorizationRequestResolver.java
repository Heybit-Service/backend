package com.heybit.backend.security.oauth;

import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthorizationRequestResolver implements OAuth2AuthorizationRequestResolver {

  private final ClientRegistrationRepository clientRegistrationRepository;
  private final DefaultOAuth2AuthorizationRequestResolver defaultResolver;
  
  @Value("${app.cors.allowed-origins}")
  private String allowedOrigins;

  public CustomAuthorizationRequestResolver(ClientRegistrationRepository clientRegistrationRepository) {
    this.clientRegistrationRepository = clientRegistrationRepository;
    this.defaultResolver = new DefaultOAuth2AuthorizationRequestResolver(
        clientRegistrationRepository, "/oauth2/authorization");
  }

  @Override
  public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
    OAuth2AuthorizationRequest authorizationRequest = defaultResolver.resolve(request);
    return processAuthorizationRequest(request, authorizationRequest);
  }

  @Override
  public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String clientRegistrationId) {
    OAuth2AuthorizationRequest authorizationRequest = defaultResolver.resolve(request, clientRegistrationId);
    return processAuthorizationRequest(request, authorizationRequest);
  }

  private OAuth2AuthorizationRequest processAuthorizationRequest(
      HttpServletRequest request, OAuth2AuthorizationRequest authorizationRequest) {
    
    if (authorizationRequest == null) {
      return null;
    }

    String referer = request.getHeader("Referer");
    String origin = extractOrigin(referer);
    
    if (origin != null && isAllowedOrigin(origin)) {
      Map<String, Object> additionalParameters = new HashMap<>(authorizationRequest.getAdditionalParameters());
      String stateData = encodeStateData(authorizationRequest.getState(), origin);
      
      return OAuth2AuthorizationRequest.from(authorizationRequest)
          .state(stateData)
          .additionalParameters(additionalParameters)
          .build();
    }
    
    return authorizationRequest;
  }

  private String extractOrigin(String referer) {
    if (referer == null || referer.isEmpty()) {
      return null;
    }
    
    try {
      URI uri = URI.create(referer);
      String port = uri.getPort() > 0 ? ":" + uri.getPort() : "";
      return uri.getScheme() + "://" + uri.getHost() + port;
    } catch (Exception e) {
      log.warn("Failed to extract origin from referer: {}", referer, e);
      return null;
    }
  }

  private boolean isAllowedOrigin(String origin) {
    if (allowedOrigins == null || origin == null) {
      return false;
    }
    
    Set<String> allowedSet = Set.of(allowedOrigins.split(","));
    boolean isAllowed = allowedSet.stream()
        .map(String::trim)
        .anyMatch(allowed -> allowed.equals(origin));
    
    log.info("Origin validation: {} is allowed: {}", origin, isAllowed);
    return isAllowed;
  }

  private String encodeStateData(String originalState, String origin) {
    String combinedData = originalState + "|" + origin;
    return Base64.getUrlEncoder().withoutPadding().encodeToString(combinedData.getBytes());
  }
}