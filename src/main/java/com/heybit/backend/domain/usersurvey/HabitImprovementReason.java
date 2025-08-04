package com.heybit.backend.domain.usersurvey;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum HabitImprovementReason {

  LACK_OF_WILLPOWER("의지가 오래가지 않아서"),
  HARD_TO_CONTROL_IMPULSE("구매 충동을 조절하기 어려워서"),
  TOO_BOTHERING("개선하는 과정이 귀찮아서"),
  DON_T_KNOW_HOW("개선 방법을 몰라서"),
  NO_VISIBLE_EFFECT("개선 효과를 느끼지 못해서");

  private final String label;
}
