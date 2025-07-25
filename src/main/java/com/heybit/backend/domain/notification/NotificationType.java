package com.heybit.backend.domain.notification;

import java.time.Duration;
import java.util.Arrays;

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
    String timeStr = formatDuration(duration);
    return switch (this) {
      case FIRST_QUARTER -> String.format("타이머 설정 시간의 25%%인 약 %s을 참았어요", timeStr);
      case SECOND_QUARTER -> String.format("타이머 설정 시간의 50%%인 약 %s을 참았어요", timeStr);
      case THIRD_QUARTER -> String.format("타이머 설정 시간의 75%%인 약 %s을 참았어요", timeStr);
      case NEARLY_DONE -> "타이머 종료까지 10분 남았어요";
      case COMPLETED -> "타이머가 종료되었어요";
    };
  }

  private String formatDuration(Duration duration) {
    long totalMinutes = duration.toMinutes();

    if (totalMinutes < 60) {
      return totalMinutes + "분";
    } else if (totalMinutes < 60 * 24) {
      long hours = totalMinutes / 60;
      long minutes = totalMinutes % 60;
      return minutes > 0 ? hours + "시간 " + minutes + "분" : hours + "시간";
    } else {
      long days = totalMinutes / (60 * 24);
      long hours = (totalMinutes % (60 * 24)) / 60;
      return hours > 0 ? days + "일 " + hours + "시간" : days + "일";
    }
  }
}
