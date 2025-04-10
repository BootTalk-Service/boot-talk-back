package com.icandoit.boottalk.batch.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.builder.SimpleJobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.icandoit.boottalk.bootcamp.service.Employ24ApiService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class BootcampBatchJobConfig {

	private final Employ24ApiService employ24ApiService;
	private final JobRepository jobRepository;
	private final PlatformTransactionManager transactionManager;

	@Bean
	public Step fetchStep_C0061_19() {
		return createFetchBootcampStep("C0061", "19");
	}

	@Bean
	public Step fetchStep_C0061_20() {
		return createFetchBootcampStep("C0061", "20");
	}

	@Bean
	public Step fetchStep_C0104_19() {
		return createFetchBootcampStep("C0104", "19");
	}

	@Bean
	public Step fetchStep_C0104_20() {
		return createFetchBootcampStep("C0104", "20");
	}

	@Bean
	public Step fetchStep_C0105_19() {
		return createFetchBootcampStep("C0105", "19");
	}

	@Bean
	public Step fetchStep_C0105_20() {
		return createFetchBootcampStep("C0105", "20");
	}

	// TODO : 알림 전송 Step
	@Bean
	public Step notifyNewBootcampStep() {
		return new StepBuilder("notifyNewBootcampStep", jobRepository)
			.tasklet((contribution, chunkContext) -> {

				log.info("알림 전송 스텝 실행: 신규 부트캠프 알림 발송 처리");
				return RepeatStatus.FINISHED;
			}, transactionManager)
			.build();
	}


	// Job 구성: 각 Step을 순차적으로 실행하도록 연결합니다.
	@Bean
	public Job bootcampFetchJob() {
		SimpleJobBuilder jobBuilder = new JobBuilder("bootcampFetchJob", jobRepository)
			.start(fetchStep_C0061_19())
			.next(fetchStep_C0061_20())
			.next(fetchStep_C0104_19())
			.next(fetchStep_C0104_20())
			.next(fetchStep_C0105_19())
			.next(fetchStep_C0105_20());
		// 추가적인 후속 스텝 (예. 알림 전송 스텝)도 연결 가능
		return jobBuilder.build();
	}

	// Step 생성 메소드 (공통 로직)
	private Step createFetchBootcampStep(String categoryCode, String ncsCode) {
		String stepName = "fetchStep_" + categoryCode + "_" + ncsCode;
		Tasklet tasklet = (contribution, chunkContext) -> {
			log.info("Step 시작: {} (categoryCode={}, ncsCode={})", stepName, categoryCode, ncsCode);
			employ24ApiService.processCategoryAndNcs(categoryCode, ncsCode);
			log.info("Step 완료: {}", stepName);
			return RepeatStatus.FINISHED;
		};

		return new StepBuilder(stepName, jobRepository)
			.tasklet(tasklet, transactionManager)
			.build();
	}
}
