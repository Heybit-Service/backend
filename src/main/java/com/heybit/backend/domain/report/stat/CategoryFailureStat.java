package com.heybit.backend.domain.report.stat;

public interface CategoryFailureStat {

  String getCategory();

  Long getFailCount();

  Long getFailAmount();
}
