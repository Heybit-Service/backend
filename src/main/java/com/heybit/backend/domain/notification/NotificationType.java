package com.heybit.backend.domain.notification;

import com.heybit.backend.global.util.DurationFormatter;
import java.time.Duration;

public enum NotificationType {
  FIRST_QUARTER("first_quarter"),
  SECOND_QUARTER("second_quarter"),
  THIRD_QUARTER("third_quarter"),
  NEARLY_DONE("nearly_done"),
  COMPLETED("completed");

  private final String code;

  NotificationType(String code) {
    this.code = code;
  }

  public String generateMessage(Duration duration) {
    String timeStr = DurationFormatter.formatCompact(duration);

    return switch (this) {
      case FIRST_QUARTER -> String.format("타이머 설정 시간의 25%%인 약 %s을 참았어요", timeStr);
      case SECOND_QUARTER -> String.format("타이머 설정 시간의 50%%인 약 %s을 참았어요", timeStr);
      case THIRD_QUARTER -> String.format("타이머 설정 시간의 75%%인 약 %s을 참았어요", timeStr);
      case NEARLY_DONE -> "타이머 종료까지 10분 남았어요";
      case COMPLETED -> "타이머가 종료되었어요";
    };
  }
}
