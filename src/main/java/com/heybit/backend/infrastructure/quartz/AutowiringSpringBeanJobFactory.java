package com.heybit.backend.infrastructure.quartz;

import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.scheduling.quartz.AdaptableJobFactory;
import org.springframework.stereotype.Component;


@Component
public class AutowiringSpringBeanJobFactory extends AdaptableJobFactory {

  @Autowired
  private AutowireCapableBeanFactory beanFactory;

  @Override
  protected Object createJobInstance(TriggerFiredBundle bundle) throws Exception {
    return beanFactory.createBean(bundle.getJobDetail().getJobClass());
  }
}
