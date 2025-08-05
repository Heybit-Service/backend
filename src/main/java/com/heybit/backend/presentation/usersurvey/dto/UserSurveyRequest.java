package com.heybit.backend.presentation.usersurvey.dto;

import com.heybit.backend.domain.usersurvey.ConsumptionTime;
import com.heybit.backend.domain.usersurvey.HabitImprovementReason;
import com.heybit.backend.domain.usersurvey.ImpulseFrequency;
import com.heybit.backend.domain.usersurvey.PurchaseTrigger;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserSurveyRequest {

  @NotNull
  private ConsumptionTime consumptionTime;

  @NotNull
  private ImpulseFrequency impulseFrequency;

  @NotNull
  private PurchaseTrigger purchaseTrigger;

  @NotNull
  private HabitImprovementReason improvementReason;

}
