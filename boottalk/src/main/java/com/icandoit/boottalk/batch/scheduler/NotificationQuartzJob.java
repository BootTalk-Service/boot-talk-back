package com.icandoit.boottalk.batch.scheduler;

import static com.icandoit.boottalk.bootcamp.service.RedisService.*;

import java.util.List;
import java.util.Set;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import com.icandoit.boottalk.bootcamp.entity.enums.BootcampCategoryType;
import com.icandoit.boottalk.bootcamp.service.RedisService;
import com.icandoit.boottalk.notification.dto.NotificationRequestDto;
import com.icandoit.boottalk.notification.service.SseEmitterService;
import com.icandoit.boottalk.notification.type.NotificationType;
import com.icandoit.boottalk.user.domain.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationQuartzJob extends QuartzJobBean {

	private final SseEmitterService sseEmitterService;
	private final RedisService redisService;
	private final UserRepository userRepository;
	private static final String BOOTCAMP_DETAIL_URL = "https://your-domain.com/api/bootcamps/";

	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		try {
			log.info("Notification Quartz Job 시작: 알림 전송 작업 실행...");

			// redis 키값 받아오고
			Set<String> keys = redisService.getKeys(BOOTCAMP_KEY_PREFIX + "*");
			// key 값이 존재한다면
			if(keys != null && !keys.isEmpty()) {
				for(String redisKey : keys) {
					// key 값에서 데이터 parsing
					String categoryString = redisKey.substring(BOOTCAMP_KEY_PREFIX.length());
					BootcampCategoryType category;

					// parsing 된 데이터 존재 값 확인
					try {
						category = BootcampCategoryType.valueOf(categoryString);
					} catch (IllegalArgumentException e) {
						log.warn("알 수 없는 부트캠프 카테고리: {}", categoryString);
						continue;
					}

					// key 값에 해당하는 value 값 조회
					List<String> bootcampIds = redisService.getList(redisKey);
					log.info("Redis key [{}] 의 부트캠프 ID: {}", redisKey, bootcampIds);

					// 해당 직군과 일치하는 userId 리스트 조회
					List<Long> targetUserIds = userRepository.findByIdsByDesiredCareer(category);

					// 해당 직군 관련 데이터들 notification Service 로 전달
					for(Long userId : targetUserIds) {
						for(String bootcampId : bootcampIds) {

							NotificationRequestDto requestDto = new NotificationRequestDto(
								NotificationType.NEW_BOOT_CAMP,
								Long.valueOf(bootcampId)
							);

							sseEmitterService.sendToClient(userId, requestDto);
						}
					}

					// 전송 완료된 key 값 삭제
					redisService.deleteKey(redisKey);
				}
			} else {
				log.info("Redis 에 부트캠프 알림 데이터가 없습니다.");
			}

			log.info("Notification Quartz Job 완료: 알림 전송 작업 종료");
		} catch (Exception e) {
			log.error("Notification Quartz Job 실행 실패", e);
			throw new JobExecutionException(e);
		}
	}
}
