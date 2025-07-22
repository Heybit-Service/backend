package com.heybit.backend.presentation.timer.dto;

import com.heybit.backend.domain.timer.TimerStatus;
import com.heybit.backend.domain.timerresult.ResultType;
import com.heybit.backend.domain.timerresult.TimerResult;
import java.time.Duration;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class CompletedTimerResponse {

  private final Long timerId;
  private final String name;
  private final boolean success;
  private final int amount;
  private final Long durationMinutes;
  private final String durationMessage;

  public static CompletedTimerResponse from(TimerResult timerResult) {
    var timer = timerResult.getProductTimer();

    LocalDateTime start = timer.getStartTime();
    LocalDateTime end = timer.getStatus() == TimerStatus.ABANDONED
        ? timerResult.getCreatedAt()
        : timer.getEndTime();

    Duration duration = Duration.between(start, end);
    long totalMinutes = duration.toMinutes();

    boolean success = timerResult.getResult() == ResultType.SAVED;

    return CompletedTimerResponse.builder()
        .timerId(timer.getId())
        .name(timer.getProductInfo().getName())
        .success(success)
        .amount(extractAmount(timerResult, success))
        .durationMinutes(totalMinutes)
        .durationMessage(formatDurationMessage(totalMinutes, success))
        .build();
  }

  private static int extractAmount(TimerResult timerResult, boolean success) {
    return success ? timerResult.getSavedAmount() : timerResult.getConsumedAmount();
  }

  private static String formatDurationMessage(long totalMinutes, boolean success) {
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
      sb.append(minutes).append("분 ");
    }

    sb.append(success ? "절제 성공" : "절제 실패");
    return sb.toString().trim();
  }

  @Override
  public String toString() {
    return "CompletedTimerResponse{" +
        "name='" + name + '\'' +
        ", success=" + success +
        ", amount=" + amount +
        ", durationMessage='" + durationMessage + '\'' +
        '}';
  }
}
