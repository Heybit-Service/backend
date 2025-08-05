package com.heybit.backend.presentation.usersurvey.dto;

import com.heybit.backend.domain.usersurvey.ConsumptionTime;
import com.heybit.backend.domain.usersurvey.HabitImprovementReason;
import com.heybit.backend.domain.usersurvey.ImpulseFrequency;
import com.heybit.backend.domain.usersurvey.PurchaseTrigger;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserSurveyRequest {

  private ConsumptionTime consumptionTime;
  private ImpulseFrequency impulseFrequency;
  private PurchaseTrigger purchaseTrigger;
  private HabitImprovementReason improvementReason;

}
