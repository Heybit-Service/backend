package com.heybit.backend.application.scheduler;

import com.heybit.backend.domain.timer.ProductTimer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TimerNotificationScheduler {

  public void scheduleTimerNotifications(ProductTimer timer) {

    //TODO: Quartz를 사용해 타이머 알림예약

    log.info("Schedule timer notifications for {}", timer.getProductInfo().getName());
  }

  public void cancelTimerNotifications(ProductTimer timer) {

    //TODO: 타이머 삭제시 타이머 알림 예약도 삭제

    log.info("Cancel timer notifications for {}", timer.getProductInfo().getName());
  }
}
