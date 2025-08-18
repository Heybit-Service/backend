package com.heybit.backend.presentation.report.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MonthlyReportResponse {

  private int year;
  private int month;

  private final List<DailySummaryResponse> dailySummaries;
  private final SuccessRateResponse successRate;
  private final List<CategoryFailureResponse> categoryFailures;
  private final DayAndTimeFailuresResponse dayAndTimeFailures;

  public MonthlyReportResponse(
      int year,
      int month,
      List<DailySummaryResponse> dailySummaries,
      SuccessRateResponse successRate,
      List<CategoryFailureResponse> categoryFailures,
      DayAndTimeFailuresResponse dayAndTimeFailures
) {
    this.year = year;
    this.month = month;
    this.dailySummaries = dailySummaries;
    this.successRate = successRate;
    this.categoryFailures = categoryFailures;
    this.dayAndTimeFailures = dayAndTimeFailures;
  }
}
