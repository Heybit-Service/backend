package com.heybit.backend.application.service;

import com.heybit.backend.domain.user.User;
import com.heybit.backend.domain.usersurvey.UserSurvey;
import com.heybit.backend.domain.usersurvey.UserSurveyRepository;
import com.heybit.backend.global.exception.ApiException;
import com.heybit.backend.global.exception.ErrorCode;
import com.heybit.backend.presentation.usersurvey.dto.UserSurveyRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserSurveyService {

  private final UserSurveyRepository userSurveyRepository;
  private final UserService userService;

  @Transactional
  public void saveSurvey(Long userId, UserSurveyRequest request) {

    User user = userService.getById(userId);

    if (userSurveyRepository.findByUser(user).isPresent()) {
      throw new ApiException(ErrorCode.ALREADY_SURVEY);
    }

    UserSurvey survey = UserSurvey.builder()
        .user(user)
        .consumptionTime(request.getConsumptionTime())
        .impulseFrequency(request.getImpulseFrequency())
        .purchaseTrigger(request.getPurchaseTrigger())
        .improvementReason(request.getImprovementReason())
        .build();

    userSurveyRepository.save(survey);
  }
}
