package com.heybit.backend.security.oauth;

import com.heybit.backend.domain.user.Role;
import java.util.Collection;
import java.util.Map;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

@Getter
public class CustomOAuth2User extends DefaultOAuth2User {

  private final String email;
  private final Long id;
  private final String nickname;
  private final Role role;

  public CustomOAuth2User(
      Collection<? extends GrantedAuthority> authorities,
      Map<String, Object> attributes,
      String nameAttributeKey,
      Long id,
      String nickname,
      String email,
      Role role
  ) {
    super(authorities, attributes, nameAttributeKey);
    this.id = id;
    this.nickname = nickname;
    this.email = email;
    this.role = role;
  }

}
