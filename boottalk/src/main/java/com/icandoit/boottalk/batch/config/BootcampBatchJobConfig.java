package com.icandoit.boottalk.batch.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.icandoit.boottalk.bootcamp.service.Employ24ApiService;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class BootcampBatchJobConfig {

	private final Employ24ApiService employ24ApiService;

	@Bean
	public Job bootcampFetchJob(JobRepository jobRepository, Step fetchBootcampStep) {
		return new JobBuilder("bootcampFetchJob", jobRepository)
			.start(fetchBootcampStep)
			.build();
	}

	@Bean
	public Step fetchBootcampStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
		return new StepBuilder("fetchBootcampStep", jobRepository)
			.tasklet(fetchBootcampTasklet(), transactionManager)
			.build();
	}

	@Bean
	public Tasklet fetchBootcampTasklet() {
		return ((contribution, chunkContext) -> {
			employ24ApiService.saveAllFromEmploy24();
			return RepeatStatus.FINISHED;
		});
	}
}
