package com.icandoit.boottalk.batch.scheduler;

import java.util.UUID;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class FetchBootcampQuartzJob extends QuartzJobBean {

	private final JobLauncher jobLauncher;
	private final Job bootcampFetchJob;

	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		try {
			JobDataMap dataMap = context.getMergedJobDataMap();
			String categoryCode = dataMap.getString("categoryCode");
			String ncsCode = dataMap.getString("ncsCode");

			JobParameters jobParameters = new JobParametersBuilder()
				.addString("jobId", UUID.randomUUID().toString())
				.addString("categoryCode", categoryCode)
				.addString("ncsCode", ncsCode)
				.toJobParameters();
			log.info("Quartz Job 시작: bootcampFetchJob 실행 중...");
			jobLauncher.run(bootcampFetchJob, jobParameters);
			log.info("Quartz Job 완료: bootcampFetchJob 실행 완료");
		} catch (Exception e) {
			log.error("Quartz Job 실행 실패", e);
			throw new JobExecutionException(e);
		}
	}
}
