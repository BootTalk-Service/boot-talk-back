package com.icandoit.boottalk.batch.config;

import org.quartz.JobDetail;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;

@Configuration
public class QuartzFetchConfig {

	@Bean
	public JobDetail fetchBootcampJobDetail() {
		JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
		factoryBean.setJobClass(com.icandoit.boottalk.batch.scheduler.FetchBootcampQuartzJob.class);
		factoryBean.setName("fetchBootcampJobDetail");
		factoryBean.setDescription("Fetch Bootcamp Data Job Detail");
		factoryBean.setDurability(true);
		factoryBean.afterPropertiesSet();
		return factoryBean.getObject();
	}
}
