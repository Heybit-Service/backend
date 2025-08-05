package com.heybit.backend.application.service;

import static org.junit.jupiter.api.Assertions.*;

import com.heybit.backend.domain.user.Role;
import com.heybit.backend.domain.user.User;
import com.heybit.backend.domain.user.UserRepository;
import com.heybit.backend.domain.user.UserStatus;
import com.heybit.backend.domain.usersurvey.ConsumptionTime;
import com.heybit.backend.domain.usersurvey.HabitImprovementReason;
import com.heybit.backend.domain.usersurvey.ImpulseFrequency;
import com.heybit.backend.domain.usersurvey.PurchaseTrigger;
import com.heybit.backend.domain.usersurvey.UserSurveyRepository;
import com.heybit.backend.presentation.usersurvey.dto.UserSurveyRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class UserSurveyServiceTest {

  @Autowired
  UserSurveyService userSurveyService;

  @Autowired
  UserRepository userRepository;

  @Autowired
  UserSurveyRepository userSurveyRepository;

  private User user;

  @BeforeEach
  void setUp() {
    user = userRepository.save(User.builder()
        .role(Role.USER)
        .status(UserStatus.ACTIVE)
        .email("test@test.com")
        .nickname("jun")
        .build());
  }

  @Test
  void submit_survey_successfully() {

    UserSurveyRequest request = new UserSurveyRequest(
        ConsumptionTime.AFTERNOON,
        ImpulseFrequency.LESS_THAN_FIVE,
        PurchaseTrigger.BORED_OR_FREE_TIME,
        HabitImprovementReason.DON_T_KNOW_HOW
    );
    userSurveyService.saveSurvey(user.getId(), request);

    assertTrue(userSurveyRepository.findByUser(user).isPresent());
  }

}