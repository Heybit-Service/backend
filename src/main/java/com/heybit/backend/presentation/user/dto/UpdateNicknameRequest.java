package com.heybit.backend.presentation.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateNicknameRequest {

  @NotBlank(message = "닉네임은 필수입니다.")
  private String nickname;
}
