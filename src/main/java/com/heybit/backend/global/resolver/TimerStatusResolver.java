package com.heybit.backend.global.resolver;

import com.heybit.backend.domain.timer.TimerStatus;
import com.heybit.backend.domain.timerresult.ResultType;

public class TimerStatusResolver {
  public static String resolveStatusCode(TimerStatus status, ResultType resultType) {
    return switch (status) {
      case IN_PROGRESS -> "IN_PROGRESS";
      case WAITING -> "WAITING";
      case ABANDONED -> "PURCHASED";
      case COMPLETED -> (resultType == ResultType.SAVED) ? "SAVED" : "PURCHASED";
    };
  }
}
