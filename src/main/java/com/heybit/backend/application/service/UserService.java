package com.heybit.backend.application.service;

import com.heybit.backend.domain.user.User;
import com.heybit.backend.domain.user.UserRepository;
import com.heybit.backend.global.exception.ApiException;
import com.heybit.backend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;

  public User getById(Long userId) {
    return userRepository.findById(userId)
        .orElseThrow(() -> {
          log.error("USER_NOT_FOUND: userId={}", userId);
          return new ApiException(ErrorCode.USER_NOT_FOUND);
        });
  }
}
