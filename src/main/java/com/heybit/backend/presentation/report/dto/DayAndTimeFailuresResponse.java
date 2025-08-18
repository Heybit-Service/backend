package com.heybit.backend.presentation.report.dto;

import java.util.Map;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DayAndTimeFailuresResponse {

  private final Map<String, Integer> byWeekday;
  private final Map<String, Integer> byTimeZone;
}
