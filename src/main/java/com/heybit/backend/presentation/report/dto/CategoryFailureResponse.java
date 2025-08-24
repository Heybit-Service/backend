package com.heybit.backend.presentation.report.dto;

import com.heybit.backend.domain.report.stat.CategoryFailureStat;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CategoryFailureResponse {

  private String category;
  private long failCount;
  private double failPercent;
  private long totalAmount;

  public static CategoryFailureResponse from(CategoryFailureStat stat, long totalFailCount) {
    double percent = totalFailCount > 0
        ? (stat.getFailCount() * 100.0 / totalFailCount)
        : 0.0;

    return new CategoryFailureResponse(
        stat.getCategory(),
        stat.getFailCount(),
        percent,
        stat.getFailAmount()
    );
  }

}
