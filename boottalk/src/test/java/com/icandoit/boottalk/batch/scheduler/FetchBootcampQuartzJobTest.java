package com.icandoit.boottalk.batch.scheduler;

import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.icandoit.boottalk.bootcamp.entity.enums.BootcampCategoryType;
import com.icandoit.boottalk.bootcamp.service.RedisService;
import com.icandoit.boottalk.notification.dto.NotificationRequestDto;
import com.icandoit.boottalk.notification.service.NotificationService;
import com.icandoit.boottalk.user.domain.repository.UserRepository;

class FetchBootcampQuartzJobTest {
	private NotificationQuartzJob notificationQuartzJob;
	private NotificationService notificationService;
	private RedisService redisService;
	private UserRepository userRepository;
	private JobExecutionContext context;

	private static final String BOOTCAMP_KEY_PREFIX = "new:bootcamp:";

	@BeforeEach
	void setUp() {
		notificationService = mock(NotificationService.class);
		redisService = mock(RedisService.class);
		userRepository = mock(UserRepository.class);
		notificationQuartzJob = new NotificationQuartzJob(notificationService, redisService, userRepository);

		// JobExecutionContext 도 목 객체로 설정
		context = mock(JobExecutionContext.class);
		JobDataMap jobDataMap = new JobDataMap();
		when(context.getMergedJobDataMap()).thenReturn(jobDataMap);
	}

	@Test
	void testExecuteInternal() throws JobExecutionException {
		// given

		// Redis에 저장된 키
		String redisKey = BOOTCAMP_KEY_PREFIX + "CLOUD_SOLUTION_ARCH";
		Set<String> keys = new HashSet<>();
		keys.add(redisKey);
		when(redisService.getKeys(BOOTCAMP_KEY_PREFIX + "*")).thenReturn(keys);

		// 해당 키에 저장된 부트캠프 ID 리스트 (예: "101", "102")
		List<String> bootcampIds = Arrays.asList("101", "102");
		when(redisService.getList(redisKey)).thenReturn(bootcampIds);

		// BootcampCategoryType 값에 맞는 사용자 ID 리스트 반환
		when(userRepository.findByIdsByDesiredCareer(BootcampCategoryType.CLOUD_SOLUTION_ARCH))
			.thenReturn(Arrays.asList(1L, 2L));

		// Quartz Job 실행
		notificationQuartzJob.executeInternal(context);

		// 각 사용자와 각 부트캠프 ID 조합에 대해 알림 전송이 2 * 2 = 4번 호출되어야 함
		verify(notificationService, times(4)).sendLiveNotification(anyLong(), any(NotificationRequestDto.class));
		// 전송 완료 후, 해당 Redis 키 삭제 호출 확인
		verify(redisService, times(1)).deleteKey(redisKey);
	}
}