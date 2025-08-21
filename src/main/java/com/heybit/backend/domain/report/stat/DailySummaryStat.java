package com.heybit.backend.domain.report.stat;

import java.time.LocalDate;

public interface DailySummaryStat {

  LocalDate getDate();

  Long getSavedAmount();

  Long getConsumedAmount();
}
