package com.icandoit.boottalk.batch.scheduler;

import java.util.List;
import java.util.Set;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import com.icandoit.boottalk.bootcamp.entity.enums.BootcampCategoryType;
import com.icandoit.boottalk.bootcamp.service.RedisService;
import com.icandoit.boottalk.common.RedisKeyPrefix;
import com.icandoit.boottalk.notification.dto.NotificationRequestDto;
import com.icandoit.boottalk.notification.event.NotificationEvent;
import com.icandoit.boottalk.notification.type.NotificationType;
import com.icandoit.boottalk.user.domain.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationQuartzJob extends QuartzJobBean {

	private final RedisService redisService;
	private final UserRepository userRepository;
	private final ApplicationEventPublisher eventPublisher;

	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		log.info("Notification Quartz Job 시작: 알림 전송 작업 실행...");
		try {
			// redis 키값 받아오고
			Set<String> keys = redisService.getKeys(RedisKeyPrefix.bootcamp() + "*");
			// key 값이 존재한다면

			if (keys == null || keys.isEmpty()) {
				log.info("Redis에 부트캠프 알림 데이터 없음.");
				return;
			}

			for (String key : keys) {
				handleBootcampKey(key);
			}

			log.info("Notification Quartz Job 완료: 알림 전송 작업 종료");
		} catch (Exception e) {
			log.error("Notification Quartz Job 전체 진행 실패", e);
			throw new JobExecutionException(e);
		}
	}

	private void handleBootcampKey(String key) {
		String categoryString = key.substring(RedisKeyPrefix.bootcamp().length());

		BootcampCategoryType category;
		try {
			category = BootcampCategoryType.valueOf(categoryString);
		} catch (IllegalArgumentException e) {
			log.warn("알 수 없는 부트캠프 카테고리: {}", categoryString);
			return;
		}

		List<String> bootcampIds = redisService.getList(key);
		if (bootcampIds.isEmpty()) {
			log.info("Redis key [{}]에 부트캠프 ID 없음", key);
			return;
		}

		List<Long> targetUserIds = userRepository.findByIdsByDesiredCareer(category);

		for (Long userId : targetUserIds) {
			try {
				publishNotificationEvent(userId);
			} catch (Exception e) {
				log.error("알림 발송 실패 - userId: {}, error: {}", userId, e.getMessage());
			}
		}

		redisService.deleteKey(key);
		log.info("처리 완료된 Redis key 삭제: {}", key);
	}

	private void publishNotificationEvent(Long userId) {
		NotificationRequestDto requestDto = new NotificationRequestDto(
			NotificationType.NEW_BOOT_CAMP,
			null
		);

		eventPublisher.publishEvent(new NotificationEvent(userId, requestDto));
		log.info("알림 발송 완료 - userId: {}", userId);
	}
}
