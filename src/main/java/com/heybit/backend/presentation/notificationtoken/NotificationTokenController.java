package com.heybit.backend.presentation.notificationtoken;

import com.heybit.backend.application.service.NotificationTokenService;
import com.heybit.backend.global.response.ApiResponseEntity;
import com.heybit.backend.presentation.notificationtoken.dto.NotificationTokenRequest;
import com.heybit.backend.security.oauth.LoginUser;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notification-tokens")
public class NotificationTokenController {

  private final NotificationTokenService tokenService;

  @PostMapping
  public ApiResponseEntity<Void> registerToken(
      @LoginUser Long userId,
      @RequestBody NotificationTokenRequest request
  ) {
    tokenService.saveOrUpdateToken(userId, request.getToken(), request.getOsType());
    return ApiResponseEntity.success();
  }
}
