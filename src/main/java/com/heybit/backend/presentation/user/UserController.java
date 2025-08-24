package com.heybit.backend.presentation.user;

import com.heybit.backend.application.service.UserService;
import com.heybit.backend.global.response.ApiResponseEntity;
import com.heybit.backend.presentation.user.dto.UpdateNicknameRequest;
import com.heybit.backend.presentation.user.dto.UserProfileResponse;
import com.heybit.backend.security.oauth.LoginUser;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

  private final UserService userService;

  @GetMapping("/me")
  public ApiResponseEntity<UserProfileResponse> getCurrentUserProfile(@LoginUser Long userId) {
    UserProfileResponse profile = userService.getUserProfile(userId);
    return ApiResponseEntity.success(profile);
  }

  @GetMapping("/check-nickname")
  public ApiResponseEntity<Boolean> checkNicknameDuplicate(@RequestParam String nickname) {
    boolean isDuplicate = userService.isNicknameDuplicated(nickname);
    return ApiResponseEntity.success(isDuplicate);
  }

  @PutMapping("/nickname")
  public ApiResponseEntity<Void> updateNickname(@LoginUser Long userId,
      @RequestBody UpdateNicknameRequest request) {
    userService.updateNickname(userId, request.getNickname());
    return ApiResponseEntity.success();
  }
}
