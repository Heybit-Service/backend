package com.heybit.backend.infrastructure.quartz;

import com.heybit.backend.application.service.NotificationService;
import com.heybit.backend.domain.notification.NotificationType;
import com.heybit.backend.domain.timer.ProductTimer;
import com.heybit.backend.domain.timer.ProductTimerRepository;
import com.heybit.backend.domain.timer.TimerStatus;
import com.heybit.backend.infrastructure.fcm.FcmService;
import java.time.Duration;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
public class NotificationJob implements Job {

  private final ProductTimerRepository timerRepository;
  private final NotificationService notificationService;
  private final FcmService fcmService;

  @Transactional
  @Override
  public void execute(JobExecutionContext context) throws JobExecutionException {
    JobDataMap dataMap = context.getMergedJobDataMap();
    Long timerId = dataMap.getLong("timerId");
    NotificationType type = (NotificationType) context.getMergedJobDataMap().get("type");

    ProductTimer timer = timerRepository.findById(timerId)
        .orElseThrow(() -> new JobExecutionException("타이머 없음"));

    // 현재 시각 기준으로 경과 시간 계산
    Duration elapsed = Duration.between(timer.getStartTime(), LocalDateTime.now());

    // 1. 알림 저장
    notificationService.save(
        timer.getProductInfo().getName(),
        timer.getUser().getId(),
        "TIMER",
        timer.getId(),
        type,
        elapsed
    );

    // 2. FCM 전송
    fcmService.sendMessage();

    // 3. 타이머 상태 업데이트
    if (type == NotificationType.COMPLETED) {
      timer.updateState(TimerStatus.COMPLETED);
    }
  }
}
