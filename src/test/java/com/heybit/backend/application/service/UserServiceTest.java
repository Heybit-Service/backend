package com.heybit.backend.application.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.heybit.backend.domain.user.Role;
import com.heybit.backend.domain.user.User;
import com.heybit.backend.domain.user.UserRepository;
import com.heybit.backend.domain.user.UserStatus;
import com.heybit.backend.global.exception.ApiException;
import com.heybit.backend.global.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;


@SpringBootTest
@Transactional
class UserServiceTest {

  @Autowired
  private UserService userService;

  @Autowired
  private UserRepository userRepository;

  private User user;

  @BeforeEach
  void setUp() {
    user = userRepository.save(User.builder()
        .nickname("jun")
        .email("jun@example.com")
        .role(Role.USER)
        .status(UserStatus.ACTIVE)
        .build());
  }

  @Test
  @DisplayName("닉네임 중복 여부 확인 - 중복일 경우 true 반환")
  void checkNicknameDuplicate_true() {
    boolean result = userService.isNicknameDuplicated(user.getNickname());
    assertThat(result).isTrue();
  }

  @Test
  @DisplayName("닉네임 중복 여부 확인 - 중복이 아닐 경우 false 반환")
  void checkNicknameDuplicate_false() {
    boolean result = userService.isNicknameDuplicated("newNickname");
    assertThat(result).isFalse();
  }

  @Test
  @DisplayName("닉네임 변경 성공")
  void updateNickname_success() {

    String nickname = "junjunjun";
    // when
    userService.updateNickname(user.getId(), nickname);

    // then
    User updated = userRepository.findById(user.getId()).orElseThrow();
    assertThat(updated.getNickname()).isEqualTo(nickname);
  }

  @Test
  @DisplayName("닉네임 변경 실패 - 중복 닉네임")
  void updateNickname_throwsException_whenDuplicateNickname() {
    String newNickname = "nickName";

    userRepository.save(User.builder()
        .role(Role.USER)
        .status(UserStatus.ACTIVE)
        .nickname(newNickname)
        .email("other@example.com")
        .build());

    ApiException exception = assertThrows(ApiException.class, () -> {
      userService.updateNickname(user.getId(), newNickname);
    });

    assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.DUPLICATE_NICKNAME);
  }

}