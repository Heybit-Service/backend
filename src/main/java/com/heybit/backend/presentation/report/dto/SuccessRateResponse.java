package com.heybit.backend.presentation.report.dto;

import com.heybit.backend.domain.report.stat.SuccessRateStat;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SuccessRateResponse {

  double successRatePercent;
  long totalCount;
  long successCount;

  public static SuccessRateResponse from(SuccessRateStat stat) {
    return new SuccessRateResponse(
        stat.successRatePercent(),
        stat.totalCount(),
        stat.successCount()
    );
  }

}
