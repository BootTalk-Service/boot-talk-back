package com.icandoit.boottalk.batch.config;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.transaction.PlatformTransactionManager;

import com.icandoit.boottalk.bootcamp.service.Employ24ApiService;

class BootcampBatchJobConfigTest {
	@Mock
	private Employ24ApiService employ24ApiService;

	@Mock
	private JobRepository jobRepository;

	@Mock
	private PlatformTransactionManager transactionManager;

	private BootcampBatchJobConfig config;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);
		config = new BootcampBatchJobConfig(employ24ApiService, jobRepository, transactionManager);
	}

	@Test
	public void testBootcampFetchJobIsNotNull() {
		// when
		Job job = config.bootcampFetchJob();

		// then
		assertNotNull(job);
	}

	@Test
	public void testFetchStepsAreNotNull() {
		// when
		Step step_C0061_19 = config.fetchStep_C0061_19();
		Step step_C0061_20 = config.fetchStep_C0061_20();
		Step step_C0104_19 = config.fetchStep_C0104_19();
		Step step_C0104_20 = config.fetchStep_C0104_20();
		Step step_C0105_19 = config.fetchStep_C0105_19();
		Step step_C0105_20 = config.fetchStep_C0105_20();

		// then: 모든 Step이 null이 아니어야 함
		assertNotNull(step_C0061_19);
		assertNotNull(step_C0061_20);
		assertNotNull(step_C0104_19);
		assertNotNull(step_C0104_20);
		assertNotNull(step_C0105_19);
		assertNotNull(step_C0105_20);
	}
}