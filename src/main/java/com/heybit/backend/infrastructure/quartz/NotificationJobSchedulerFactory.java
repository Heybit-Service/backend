package com.heybit.backend.infrastructure.quartz;

import com.heybit.backend.domain.notification.NotificationType;
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

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationJobSchedulerFactory {

  private final Scheduler scheduler;

  public void register(Long timerId, String productName,
      LocalDateTime scheduleTime, NotificationType type
  ) {
    JobDataMap jobDataMap = new JobDataMap();
    jobDataMap.put("timerId", timerId);
    jobDataMap.put("name", productName);
    jobDataMap.put("type", type);

    JobDetail jobDetail = JobBuilder.newJob(NotificationJob.class)
        .withIdentity("NotificationJob_" + type, "TIMER_" + timerId)  // group = timerId
        .usingJobData(jobDataMap)
        .build();

    Trigger trigger = TriggerBuilder.newTrigger()
        .withIdentity("NotificationTrigger_" + type, "TIMER_" + timerId)
        .startAt(Date.from(scheduleTime.atZone(ZoneId.of("Asia/Seoul")).toInstant()))
        .build();

    try {
      scheduler.scheduleJob(jobDetail, trigger);
      log.info("Notification job 등록: timerId={}, type={}", timerId, type);
    } catch (SchedulerException e) {
      log.error("스케줄 등록 실패", e);
      throw new RuntimeException(e);
    }
  }


  public void cancelAllByTimerId(Long timerId) {

    for (NotificationType type : NotificationType.values()) {
      JobKey jobKey = JobKey.jobKey("NotificationJob_" + type, "TIMER_" + timerId);
      try {
        if (scheduler.checkExists(jobKey)) {
          scheduler.deleteJob(jobKey);
          log.info("Job 삭제: {}", jobKey.getName());
        }
      } catch (SchedulerException e) {
        log.error("Job 삭제 실패: timerId={}, type={}", timerId, type, e);
      }
    }
  }
}
