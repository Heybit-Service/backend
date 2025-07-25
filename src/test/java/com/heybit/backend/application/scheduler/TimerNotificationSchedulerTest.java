package com.heybit.backend.application.scheduler;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.heybit.backend.infrastructure.quartz.NotificationJobSchedulerFactory;
import java.time.LocalDateTime;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.matchers.GroupMatcher;
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

  private final Long testTimerId = 999L;
  private final String testName = "Test Timer";

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
}