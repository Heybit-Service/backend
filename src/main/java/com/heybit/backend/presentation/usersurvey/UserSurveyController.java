package com.heybit.backend.presentation.usersurvey;

import com.heybit.backend.application.service.UserSurveyService;
import com.heybit.backend.global.response.ApiResponseEntity;
import com.heybit.backend.presentation.usersurvey.dto.UserSurveyRequest;
import com.heybit.backend.security.oauth.LoginUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/surveys")
public class UserSurveyController {

  private final UserSurveyService userSurveyService;

  @PostMapping
  public ApiResponseEntity<Void> submitSurvey(
      @RequestBody UserSurveyRequest request,
      @LoginUser Long userId
  ) {
    userSurveyService.saveSurvey(userId, request);
    return ApiResponseEntity.success();
  }
}
