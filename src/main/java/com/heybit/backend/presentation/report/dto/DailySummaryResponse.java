package com.heybit.backend.presentation.report.dto;

import com.heybit.backend.domain.report.stat.DailySummaryStat;
import java.time.format.DateTimeFormatter;
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
        .consumedAmount(stat.getConsumedAmount())
        .savedAmount(stat.getSavedAmount())
        .day(stat.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
        .build();
  }
}
