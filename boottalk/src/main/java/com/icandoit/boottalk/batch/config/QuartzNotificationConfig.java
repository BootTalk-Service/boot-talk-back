package com.icandoit.boottalk.batch.config;

import java.text.ParseException;

import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;

@Configuration
public class QuartzNotificationConfig {

	@Bean
	public JobDetail notificationJobDetail() {
		JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
		factoryBean.setJobClass(com.icandoit.boottalk.batch.scheduler.NotificationQuartzJob.class);
		factoryBean.setName("notificationJobDetail");
		factoryBean.setDescription("Notification Job Detail");
		factoryBean.setDurability(true);
		factoryBean.afterPropertiesSet();
		return factoryBean.getObject();
	}

	@Bean
	public Trigger notificationTrigger(JobDetail notificationJobDetail) throws ParseException {
		CronTriggerFactoryBean triggerFactoryBean = new CronTriggerFactoryBean();
		triggerFactoryBean.setJobDetail(notificationJobDetail);
		// 매일 12시에 실행
		triggerFactoryBean.setCronExpression("0 59 23 * * ?");
		triggerFactoryBean.setName("notificationTrigger");
		triggerFactoryBean.afterPropertiesSet();
		return triggerFactoryBean.getObject();
	}
}
