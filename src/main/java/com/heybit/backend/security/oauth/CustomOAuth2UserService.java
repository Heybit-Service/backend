package com.heybit.backend.security.oauth;

import com.heybit.backend.domain.user.User;
import com.heybit.backend.domain.user.UserRepository;
import com.heybit.backend.domain.user.UserStatus;
import com.heybit.backend.global.exception.ApiException;
import com.heybit.backend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

  private final UserRepository userRepository;

  @Override
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

    OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
    OAuth2User oauth2User = delegate.loadUser(userRequest);

    String registrationId = userRequest.getClientRegistration().getRegistrationId();
    String userNameAttributeName = userRequest.getClientRegistration()
        .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

    OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName,
        oauth2User.getAttributes());

    String email = attributes.getEmail();

    User user = userRepository.findByEmail(email)
        .map(existingUser -> {
          if (existingUser.getStatus() == UserStatus.DELETED) {
            throw new OAuth2AuthenticationProcessingException(ErrorCode.DELETED_USER);
          }
          return existingUser;
        })
        .orElseGet(() -> userRepository.save(attributes.toEntity()));

    return new CustomOAuth2User(
        oauth2User.getAuthorities(),
        oauth2User.getAttributes(),
        userNameAttributeName,
        user.getId(),
        user.getNickname(),
        user.getEmail(),
        user.getRole()
    );
  }

}

