package com.heybit.backend.presentation.notificationtoken.dto;

import com.heybit.backend.domain.notificationtoken.OsType;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NotificationTokenRequest {
  private String token;
  private OsType osType;
}