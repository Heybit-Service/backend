package com.heybit.backend.presentation.report.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommonReportResponse {
  private final SuccessRateResponse successRate;
  private final List<CategoryFailureResponse> categoryFailures;
  private final DayAndTimeRegisteredCountsResponse registeredCounts;
}
