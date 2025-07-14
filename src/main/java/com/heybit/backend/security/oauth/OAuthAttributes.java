package com.heybit.backend.security.oauth;


import com.heybit.backend.domain.user.Role;
import com.heybit.backend.domain.user.User;
import com.heybit.backend.domain.user.UserStatus;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;

@Getter
public class OAuthAttributes {

  private final Map<String, Object> attributes;
  private final String nameAttributeKey;
  private final String nickname;
  private final String email;
  private final String provider;

  @Builder
  public OAuthAttributes(Map<String, Object> attributes, String nameAttributeKey, String nickname,
      String email, String provider) {
    this.attributes = attributes;
    this.nameAttributeKey = nameAttributeKey;
    this.nickname = nickname;
    this.email = email;
    this.provider = provider;
  }

  public static OAuthAttributes of(String registrationId, String userNameAttributeName,
      Map<String, Object> attributes) {
    if ("kakao".equals(registrationId)) {
      return ofKakao(userNameAttributeName, attributes, registrationId);
    }

    return ofGoogle(userNameAttributeName, attributes, registrationId);
  }

  private static OAuthAttributes ofGoogle(String userNameAttributeName,
      Map<String, Object> attributes, String provider) {
    return OAuthAttributes.builder()
        .email((String) attributes.get("email"))
        .provider(provider)
        .attributes(attributes)
        .nameAttributeKey(userNameAttributeName)
        .build();
  }

  private static OAuthAttributes ofKakao(String userNameAttributeName,
      Map<String, Object> attributes, String provider) {
    Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");

    return OAuthAttributes.builder()
        .email((String) kakaoAccount.get("email"))
        .provider(provider)
        .attributes(attributes)
        .nameAttributeKey(userNameAttributeName)
        .build();
  }

  public User toEntity() {
    return User.builder()
        .email(email)
        .nickname(null)
        .role(Role.USER)
        .status(UserStatus.ACTIVE)
        .provider(provider)
        .build();
  }
}
