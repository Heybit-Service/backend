package com.heybit.backend.infrastructure.quartz;

import com.heybit.backend.domain.notification.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

@Slf4j
@RequiredArgsConstructor
public class NotificationJob implements Job {

  private final JobService jobService;

  @Override
  public void execute(JobExecutionContext context) throws JobExecutionException {
    JobDataMap dataMap = context.getMergedJobDataMap();
    Long timerId = dataMap.getLong("timerId");
    NotificationType type = (NotificationType) context.getMergedJobDataMap().get("type");
    jobService.handleNotificationJob(timerId, type);
  }
}
