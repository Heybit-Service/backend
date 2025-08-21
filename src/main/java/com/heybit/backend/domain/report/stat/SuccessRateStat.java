package com.heybit.backend.domain.report.stat;

public interface SuccessRateStat {

  Double getSuccessRate();

  Long getTotalCount();

  Long getSuccessCount();
}
