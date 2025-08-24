package com.heybit.backend.application.service;

import com.heybit.backend.domain.user.User;
import com.heybit.backend.domain.user.UserRepository;
import com.heybit.backend.global.exception.ApiException;
import com.heybit.backend.global.exception.ErrorCode;
import com.heybit.backend.presentation.user.dto.UserProfileResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;

  @Transactional
  public void updateNickname(Long userId, String newNickname) {
    if (isNicknameDuplicated(newNickname)) {
      throw new ApiException(ErrorCode.DUPLICATE_NICKNAME);
    }

    User user = getById(userId);
    user.changeNickname(newNickname);
  }

  @Transactional(readOnly = true)
  public boolean isNicknameDuplicated(String nickname) {
    return userRepository.existsByNickname(nickname);
  }

  @Transactional
  public User getById(Long userId) {
    return userRepository.findById(userId)
        .orElseThrow(() -> {
          log.error("USER_NOT_FOUND: userId={}", userId);
          return new ApiException(ErrorCode.USER_NOT_FOUND);
        });
  }

  @Transactional(readOnly = true)
  public UserProfileResponse getUserProfile(Long userId) {
    User user = getById(userId);
    return UserProfileResponse.builder()
        .id(user.getId())
        .email(user.getEmail())
        .nickname(user.getNickname())
        .role(user.getRole())
        .status(user.getStatus())
        .build();
  }
}
