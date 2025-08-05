package com.heybit.backend.domain.usersurvey;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ConsumptionTime {

  MORNING("오전 (06~11시)"),
  LUNCH("점심시간 (11~14시)"),
  AFTERNOON("오후 (14~17시)"),
  EVENING("저녁 (17~20시)"),
  NIGHT("밤 (20~24시)"),
  DAWN("새벽 (00~06시)");

  private final String label;
}
