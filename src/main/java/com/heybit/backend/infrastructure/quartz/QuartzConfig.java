package com.heybit.backend.infrastructure.quartz;

import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

@Configuration
@RequiredArgsConstructor
public class QuartzConfig {

  private final AutowireCapableBeanFactory beanFactory;
  private final DataSource dataSource;

  @Value("${spring.quartz.job-store-type}")
  private String jobStoreType;

  @Bean
  public SchedulerFactoryBean schedulerFactoryBean(AutowiringSpringBeanJobFactory jobFactory) {
    SchedulerFactoryBean factory = new SchedulerFactoryBean();
    factory.setJobFactory(jobFactory);
    factory.setOverwriteExistingJobs(false);
    factory.setWaitForJobsToCompleteOnShutdown(true);
    
    if ("jdbc".equalsIgnoreCase(jobStoreType)) {
      factory.setDataSource(dataSource);
    }

    return factory;
  }

  @Bean
  public Scheduler scheduler(SchedulerFactoryBean factory) throws SchedulerException {
    return factory.getScheduler();
  }
}
