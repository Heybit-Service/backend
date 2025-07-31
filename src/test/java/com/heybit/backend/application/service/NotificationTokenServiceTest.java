package com.heybit.backend.application.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.heybit.backend.domain.notificationtoken.NotificationToken;
import com.heybit.backend.domain.notificationtoken.NotificationTokenRepository;
import com.heybit.backend.domain.notificationtoken.OsType;
import com.heybit.backend.domain.user.Role;
import com.heybit.backend.domain.user.User;
import com.heybit.backend.domain.user.UserRepository;
import com.heybit.backend.domain.user.UserStatus;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class NotificationTokenServiceTest {

  @Autowired
  private NotificationTokenService tokenService;

  @Autowired
  private NotificationTokenRepository tokenRepository;

  @Autowired
  private UserRepository userRepository;

  @Test
  void FCM_Token_save_successfully() {
    // given
    User user = userRepository.save(User.builder()
        .nickname("jun")
        .email("jun@example.com")
        .role(Role.USER)
        .status(UserStatus.ACTIVE)
        .build());

    String token = "newToken";

    // when
    tokenService.saveOrUpdateToken(user.getId(), token, OsType.ANDROID);

    // then
    List<NotificationToken> saved = tokenRepository.findAllByUserId(user.getId());
    assertThat(saved).hasSize(1);
    assertThat(saved.get(0).getToken()).isEqualTo(token);
    assertThat(saved.get(0).getOsType()).isEqualTo(OsType.ANDROID);
  }

  @Test
  void FCM_Token_update_successfully() {
    // given
    User user = userRepository.save(User.builder()
        .nickname("jun")
        .email("jun@example.com")
        .role(Role.USER)
        .status(UserStatus.ACTIVE)
        .build());

    tokenRepository.save(NotificationToken.builder()
        .user(user)
        .token("oldToken")
        .osType(OsType.ANDROID)
        .build());

    // when
    String updatedToken = "newToken";
    tokenService.saveOrUpdateToken(user.getId(), updatedToken, OsType.ANDROID);

    // then
    List<NotificationToken> tokens = tokenRepository.findAllByUserId(user.getId());
    assertThat(tokens).hasSize(1);
    assertThat(tokens.get(0).getToken()).isEqualTo(updatedToken);
  }
}