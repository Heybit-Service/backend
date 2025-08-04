package com.heybit.backend.infrastructure.quartz;

import com.heybit.backend.application.service.NotificationService;
import com.heybit.backend.domain.notification.NotificationType;
import com.heybit.backend.domain.notification.ReferenceType;
import com.heybit.backend.domain.timer.ProductTimer;
import com.heybit.backend.domain.timer.ProductTimerRepository;
import com.heybit.backend.domain.timer.TimerStatus;
import com.heybit.backend.infrastructure.fcm.FcmService;
import java.time.Duration;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class JobService {

  private final ProductTimerRepository timerRepository;
  private final NotificationService notificationService;
  private final FcmService fcmService;

  @Transactional
  public void handleNotificationJob(Long timerId, NotificationType type
  ) throws JobExecutionException {

    ProductTimer timer = timerRepository.findById(timerId)
        .orElseThrow(() -> new JobExecutionException("타이머 없음"));

    // 현재 시각 기준으로 경과 시간 계산
    Duration elapsed = Duration.between(timer.getStartTime(), LocalDateTime.now());

    // 1. 알림 저장
    notificationService.save(
        timer.getProductInfo().getName(),
        timer.getUser().getId(),
        ReferenceType.PRODUCT_TIMER,
        timer.getId(),
        type,
        elapsed
    );

    // 2. FCM 전송
    fcmService.sendMessage();

    // 3. 타이머 상태 업데이트
    if (type == NotificationType.COMPLETED) {
      timer.updateState(TimerStatus.WAITING);
    }
  }
}
