package com.icandoit.boottalk.batch.scheduler;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import com.icandoit.boottalk.notification.service.NotificationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationQuartzJob extends QuartzJobBean {

	private final NotificationService notificationService;

	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		try {
			log.info("Notification Quartz Job 시작: 알림 전송 작업 실행...");
			// TODO : 실제 알림 전송 기능을 호출 (추후 구현 예정)
			// notificationService.sendNewBootcampNotifications();
			log.info("Notification Quartz Job 완료: 알림 전송 작업 종료");
		} catch (Exception e) {
			log.error("Notification Quartz Job 실행 실패", e);
			throw new JobExecutionException(e);
		}
	}
}
