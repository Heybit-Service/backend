package com.heybit.backend.domain.usersurvey;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ImpulseFrequency {

  NONE("없음"),
  ONE_TO_TWO("1~2회"),
  LESS_THAN_FIVE("5회 미만"),
  MORE_THAN_FIVE("5회 이상");

  private final String label;
}
