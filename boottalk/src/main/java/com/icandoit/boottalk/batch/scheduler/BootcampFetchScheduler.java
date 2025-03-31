package com.icandoit.boottalk.batch.scheduler;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class BootcampFetchScheduler {

	private final JobLauncher jobLauncher;
	private final Job bootcampFetchJob;

	@Scheduled(cron = "0 0 0 * * *") // 매일 자정
	public void runBootcampFetchJob() {
		try {
			log.info("Bootcamp Fetch Batch 실행 시작 : {}", LocalDateTime.now());

			JobParameters parameters = new JobParametersBuilder()
				.addString("jobId", UUID.randomUUID().toString())
				.toJobParameters();

			jobLauncher.run(bootcampFetchJob, parameters);

			log.info("Bootcamp Fetch Batch 실행 완료");
		} catch (Exception e) {
			log.error("Bootcamp Fetch Batch 실행 실패", e);
		}
	}
}
