package com.heybit.backend.presentation.report.dto;

import com.heybit.backend.domain.report.stat.DailySummaryStat;
import com.heybit.backend.domain.report.stat.MonthSaveStat;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DailySummaryResponse {

  private final String day;
  private final long savedAmount;
  private final long consumedAmount;

  public static DailySummaryResponse from(DailySummaryStat stat) {
    return DailySummaryResponse.builder()
        .consumedAmount(stat.consumedAmount())
        .savedAmount(stat.savedAmount())
        .day(stat.day())
        .build();
  }
}
