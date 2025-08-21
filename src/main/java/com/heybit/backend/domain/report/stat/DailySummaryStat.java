package com.heybit.backend.domain.report.stat;

import java.time.LocalDateTime;

public interface DailySummaryStat {

  LocalDateTime getDate();

  Long getSavedAmount();

  Long getConsumedAmount();
}
