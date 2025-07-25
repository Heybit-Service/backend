package com.heybit.backend.application.scheduler;

import com.heybit.backend.domain.notification.NotificationType;
import com.heybit.backend.domain.timer.ProductTimer;
import com.heybit.backend.infrastructure.quartz.NotificationJob;
import com.heybit.backend.infrastructure.quartz.NotificationJobSchedulerFactory;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
@RequiredArgsConstructor
@Slf4j
public class TimerNotificationScheduler {

  private final NotificationJobSchedulerFactory schedulerFactory;

  public void scheduleTimerNotificationJob(
      LocalDateTime start, LocalDateTime end, Long timerId, String name
  ) {
    long duration = Duration.between(start, end).getSeconds();

    if (duration <= 1800) {
      // 30분 이하 타이머
      schedulerFactory.register(
          timerId,
          name,
          start.plusSeconds(duration / 2),
          NotificationType.SECOND_QUARTER
      );

      schedulerFactory.register(
          timerId,
          name,
          end,
          NotificationType.COMPLETED)
      ;
    } else if (duration <= 86400) {
      // 24시간 이하 타이머
      schedulerFactory.register(
          timerId,
          name,
          start.plusSeconds(duration / 2),
         NotificationType.SECOND_QUARTER
      );

      schedulerFactory.register(
          timerId,
          name,
          end.minusMinutes(10),
          NotificationType.NEARLY_DONE
      );

      schedulerFactory.register(
          timerId,
          name,
          end,
          NotificationType.COMPLETED
      );
    } else {
      // 24시간 초과 타이머
      schedulerFactory.register(
          timerId,
          name,
          start.plusSeconds(duration / 4),
          NotificationType.FIRST_QUARTER)
      ;

      schedulerFactory.register(
          timerId,
          name,
          start.plusSeconds(duration * 3 / 4),
          NotificationType.THIRD_QUARTER
      );

      schedulerFactory.register(
          timerId,
          name,
          end.minusMinutes(10),
          NotificationType.NEARLY_DONE)
      ;

      schedulerFactory.register(
          timerId,
          name,
          end,
          NotificationType.COMPLETED
      );
    }
  }

  public void cancelTimerNotificationJob(Long timerId) {
    schedulerFactory.cancelAllByTimerId(timerId);
  }

}
