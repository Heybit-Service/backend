package com.heybit.backend.presentation.user.dto;

import com.heybit.backend.domain.user.Role;
import com.heybit.backend.domain.user.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileResponse {

  private Long id;
  private String email;
  private String nickname;
  private Role role;
  private UserStatus status;

}