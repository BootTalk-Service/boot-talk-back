package com.icandoit.boottalk.batch.config;

import java.text.ParseException;

import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;

@Configuration
public class QuartzFetchTriggersConfig {

	private final JobDetail fetchBootcampJobDetail;

	public QuartzFetchTriggersConfig(JobDetail fetchBootcampJobDetail) {
		this.fetchBootcampJobDetail = fetchBootcampJobDetail;
	}

	@Bean
	public Trigger fetchTrigger_C0061_19() throws ParseException {
		CronTriggerFactoryBean triggerFactoryBean = new CronTriggerFactoryBean();
		triggerFactoryBean.setJobDetail(fetchBootcampJobDetail);
		// 매일 11시 25분에 실행
		triggerFactoryBean.setCronExpression("0 25 23 * * ?");
		triggerFactoryBean.setName("fetchTrigger_C0061_19");

		JobDataMap dataMap = new JobDataMap();
		dataMap.put("categoryCode", "C0061");
		dataMap.put("ncsCode", "19");
		triggerFactoryBean.setJobDataMap(dataMap);

		triggerFactoryBean.afterPropertiesSet();
		return triggerFactoryBean.getObject();
	}

	// 나머지 트리거들도 유사하게 정의 (크론 표현식을 5분 간격으로 설정)
	@Bean
	public Trigger fetchTrigger_C0061_20() throws ParseException {
		CronTriggerFactoryBean triggerFactoryBean = new CronTriggerFactoryBean();
		triggerFactoryBean.setJobDetail(fetchBootcampJobDetail);
		// 매일 11시 30분에 실행
		triggerFactoryBean.setCronExpression("0 30 23 * * ?");
		triggerFactoryBean.setName("fetchTrigger_C0061_20");
		JobDataMap dataMap = new JobDataMap();
		dataMap.put("categoryCode", "C0061");
		dataMap.put("ncsCode", "20");
		triggerFactoryBean.setJobDataMap(dataMap);
		triggerFactoryBean.afterPropertiesSet();
		return triggerFactoryBean.getObject();
	}

	@Bean
	public Trigger fetchTrigger_C0104_19() throws ParseException {
		CronTriggerFactoryBean triggerFactoryBean = new CronTriggerFactoryBean();
		triggerFactoryBean.setJobDetail(fetchBootcampJobDetail);
		// 매일 11시 35분에 실행
		triggerFactoryBean.setCronExpression("0 35 23 * * ?");
		triggerFactoryBean.setName("fetchTrigger_C0104_19");
		JobDataMap dataMap = new JobDataMap();
		dataMap.put("categoryCode", "C0104");
		dataMap.put("ncsCode", "19");
		triggerFactoryBean.setJobDataMap(dataMap);
		triggerFactoryBean.afterPropertiesSet();
		return triggerFactoryBean.getObject();
	}

	@Bean
	public Trigger fetchTrigger_C0104_20() throws ParseException {
		CronTriggerFactoryBean triggerFactoryBean = new CronTriggerFactoryBean();
		triggerFactoryBean.setJobDetail(fetchBootcampJobDetail);
		// 매일 11시 40분에 실행
		triggerFactoryBean.setCronExpression("0 40 23 * * ?");
		triggerFactoryBean.setName("fetchTrigger_C0104_20");
		JobDataMap dataMap = new JobDataMap();
		dataMap.put("categoryCode", "C0104");
		dataMap.put("ncsCode", "20");
		triggerFactoryBean.setJobDataMap(dataMap);
		triggerFactoryBean.afterPropertiesSet();
		return triggerFactoryBean.getObject();
	}

	@Bean
	public Trigger fetchTrigger_C0105_19() throws ParseException {
		CronTriggerFactoryBean triggerFactoryBean = new CronTriggerFactoryBean();
		triggerFactoryBean.setJobDetail(fetchBootcampJobDetail);
		// 매일 11시 45분에 실행
		triggerFactoryBean.setCronExpression("0 45 23 * * ?");
		triggerFactoryBean.setName("fetchTrigger_C0105_19");
		JobDataMap dataMap = new JobDataMap();
		dataMap.put("categoryCode", "C0105");
		dataMap.put("ncsCode", "19");
		triggerFactoryBean.setJobDataMap(dataMap);
		triggerFactoryBean.afterPropertiesSet();
		return triggerFactoryBean.getObject();
	}

	@Bean
	public Trigger fetchTrigger_C0105_20() throws ParseException {
		CronTriggerFactoryBean triggerFactoryBean = new CronTriggerFactoryBean();
		triggerFactoryBean.setJobDetail(fetchBootcampJobDetail);
		// 매일 11시 50분에 실행
		triggerFactoryBean.setCronExpression("0 50 23 * * ?");
		triggerFactoryBean.setName("fetchTrigger_C0105_20");
		JobDataMap dataMap = new JobDataMap();
		dataMap.put("categoryCode", "C0105");
		dataMap.put("ncsCode", "20");
		triggerFactoryBean.setJobDataMap(dataMap);
		triggerFactoryBean.afterPropertiesSet();
		return triggerFactoryBean.getObject();
	}
}
