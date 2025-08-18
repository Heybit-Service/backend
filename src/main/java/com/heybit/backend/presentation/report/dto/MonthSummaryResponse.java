package com.heybit.backend.presentation.report.dto;

import com.heybit.backend.domain.report.stat.MonthSaveStat;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MonthSummaryResponse {

  private final int year;
  private final int month;
  private final long savedAmount;

  public static MonthSummaryResponse from(MonthSaveStat stat) {
    return MonthSummaryResponse.builder()
        .year(stat.year())
        .month(stat.month())
        .savedAmount(stat.savedAmount())
        .build();
  }
}
