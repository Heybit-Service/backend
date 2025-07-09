package com.heybit.backend.domain.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserStatus {
  ACTIVE("정상 회원"),
  DELETED("탈퇴 회원");

  private final String title;
}
