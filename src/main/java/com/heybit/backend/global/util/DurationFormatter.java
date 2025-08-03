package com.heybit.backend.global.util;

import java.time.Duration;

public class DurationFormatter {

  private DurationFormatter() {
    // static utility class
  }

  public static String formatCompact(Duration duration) {
    long totalMinutes = duration.toMinutes();
    long days = totalMinutes / (60 * 24);
    long hours = (totalMinutes % (60 * 24)) / 60;
    long minutes = totalMinutes % 60;

    StringBuilder sb = new StringBuilder();
    if (days > 0) {
      sb.append(days).append("일 ");
    }
    if (hours > 0) {
      sb.append(hours).append("시간 ");
    }
    if (minutes > 0) {
      sb.append(minutes).append("분");
    }
    return sb.toString().trim();
  }

  public static String formatWithSuffix(Duration duration, boolean success) {
    return formatCompact(duration) + (success ? " 절제 성공" : " 절제 실패");
  }
}

