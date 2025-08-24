package com.heybit.backend.presentation.report.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TotalReportResponse {

  private final long totalSavedAmount;
  private final List<MonthSummaryResponse> monthSummaries;
  private final SuccessRateResponse successRate;
  private final List<CategoryFailureResponse> categoryFailures;
  private final DayAndTimeRegisteredCountsResponse registeredCounts;

  public TotalReportResponse(
      long totalSavedAmount,
      List<MonthSummaryResponse> monthSummaries,
      SuccessRateResponse successRate,
      List<CategoryFailureResponse> categoryFailures,
      DayAndTimeRegisteredCountsResponse registeredCounts
  ) {
    this.totalSavedAmount = totalSavedAmount;
    this.monthSummaries = monthSummaries;
    this.successRate = successRate;
    this.categoryFailures = categoryFailures;
    this.registeredCounts = registeredCounts;
  }
}
