package com.icandoit.boottalk.notification.service;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.icandoit.boottalk.notification.dto.AllNotificationResponseDto;
import com.icandoit.boottalk.notification.dto.NotificationResponseDto;
import com.icandoit.boottalk.notification.entity.Notification;
import com.icandoit.boottalk.notification.repository.NotificationRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

	private final NotificationRepository notificationRepository;

	@Value("${server.url}")
	private String BASE_URL;

	// 알림조회창에 들어갈 알림내역과 확인하지 않은 알림 개수 반환
	public AllNotificationResponseDto getNotifications(long userId) {
		return AllNotificationResponseDto.from(notificationRepository.findAllNotificationByUserId(userId)
			.stream().map((Notification notification) ->
				NotificationResponseDto.from(notification, BASE_URL)).collect(Collectors.toList()));
	}


	// 일괄 확인 처리
	// 확인한 알림 중에서 가장 최신 알림의 생성일자를 받아와 해당 일자 이전인 알림만 확인 처리
	// 아니면 알림창을 닫을 때의 시간 데이터를 가져와서 처리
	public void checkedAllNotification(long userId, LocalDateTime time) {
		int checkedCount = notificationRepository.checkedAllNotification(userId, time);
		log.debug("확인 처리된 알림 개수 : {}", checkedCount);
	}
}
