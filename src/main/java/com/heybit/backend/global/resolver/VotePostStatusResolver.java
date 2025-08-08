package com.heybit.backend.global.resolver;

import com.heybit.backend.domain.timer.TimerStatus;
import com.heybit.backend.domain.timerresult.ResultType;

public class VotePostStatusResolver {
  public static String resolveStatus(TimerStatus status, ResultType resultType) {
    return switch (status) {
      case IN_PROGRESS -> "투표 중";
      case WAITING -> "결과 미등록";
      case ABANDONED -> "결제 실패";
      case COMPLETED -> (resultType == ResultType.SAVED) ? "결제 성공" : "결제 실패";
    };
  }
}
