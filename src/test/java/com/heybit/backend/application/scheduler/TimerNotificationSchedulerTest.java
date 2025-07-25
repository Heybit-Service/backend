package com.heybit.backend.application.scheduler;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.heybit.backend.infrastructure.quartz.NotificationJobSchedulerFactory;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TimerNotificationSchedulerTest {

  @Autowired
  private TimerNotificationScheduler notificationScheduler;

  @Autowired
  private NotificationJobSchedulerFactory jobSchedulerFactory;

  @Autowired
  private Scheduler scheduler;

  @BeforeEach
  void setUp() throws SchedulerException {
    scheduler.clear();
  }

  @Test
  @DisplayName("30분 타이머 등록시 알림 스케줄링 예약 검증")
  void JobRegistration_30MinutesTimer() throws Exception {
    // given
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime end = now.plusMinutes(20);
    Long timerId = 1L;
    String name = "TestTimer";

    // when
    notificationScheduler.scheduleTimerNotificationJob(now, end, timerId, name);

    // then
    assertTrue(scheduler.checkExists(JobKey.jobKey("NotificationJob_second_quarter", "TIMER_1")));
    assertTrue(scheduler.checkExists(JobKey.jobKey("NotificationJob_completed", "TIMER_1")));
  }

  @Test
  @DisplayName("타이머가 삭제될떄 해당 id의 모든 스케줄 삭제 검증")
  void cancelAllByTimerId() throws Exception {
    // given
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime end = now.plusMinutes(60);
    Long timerId = 2L;
    String name = "ToDeleteTimer";

    notificationScheduler.scheduleTimerNotificationJob(now, end, timerId, name);

    // 사전 확인
    assertTrue(scheduler.checkExists(JobKey.jobKey("NotificationJob_second_quarter", "TIMER_2")));
    assertTrue(scheduler.checkExists(JobKey.jobKey("NotificationJob_nearly_done", "TIMER_2")));
    assertTrue(scheduler.checkExists(JobKey.jobKey("NotificationJob_completed", "TIMER_2")));

    // when
    notificationScheduler.cancelTimerNotificationJob(timerId);

    // then
    assertFalse(scheduler.checkExists(JobKey.jobKey("NotificationJob_second_quarter", "TIMER_2")));
    assertFalse(scheduler.checkExists(JobKey.jobKey("NotificationJob_nearly_done", "TIMER_2")));
    assertFalse(scheduler.checkExists(JobKey.jobKey("NotificationJob_completed", "TIMER_2")));
  }
}