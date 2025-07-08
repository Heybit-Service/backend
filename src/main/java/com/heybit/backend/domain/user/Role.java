package com.heybit.backend.domain.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
  MASTER("ROLE_MASTER", "관리자"),
  USER("ROLE_USER", "유저");

  private final String key;
  private final String title;
}
