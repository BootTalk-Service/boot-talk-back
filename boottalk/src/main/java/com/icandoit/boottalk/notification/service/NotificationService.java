package com.icandoit.boottalk.notification.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.icandoit.boottalk.notification.dto.AllNotificationResponseDto;
import com.icandoit.boottalk.notification.dto.NotificationRequestDto;
import com.icandoit.boottalk.notification.dto.NotificationResponseDto;
import com.icandoit.boottalk.notification.entity.Notification;
import com.icandoit.boottalk.notification.repository.NotificationRepository;
import com.icandoit.boottalk.notification.util.DateTimeFormatterUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

	private final NotificationRepository notificationRepository;
	private final DateTimeFormatterUtil dateTimeFormatterUtil;
	private final SseEmitterService emitterService;


	//SSE 연결
	public SseEmitter connect(long userId, String lastEventId) {

		SseEmitter sseEmitter = emitterService.subscribe(userId);

		//SSE 연결이 비정상적으로 종료되었을 때 lastEventId를 통해 놓친 알림 전송
		//lastEventId 이후에 온 알림이 있다면 해당 알림을 다시 실시간으로 전송
		if (lastEventId != null) {
			List<Notification> notifications = notificationRepository.findByMissedNotifications(
				userId, dateTimeFormatterUtil.parseTime(lastEventId));
			if (!notifications.isEmpty()) {
				for (Notification notification : notifications) {
					emitterService.sendToClient(userId, NotificationResponseDto.from(notification));
				}
			}
		}

		return sseEmitter;
	}


	// 알림을 저장하고 알림 대상자에게 전송
	public NotificationResponseDto sendLiveNotification(long userId, NotificationRequestDto requestDto) {
		NotificationResponseDto responseDto = NotificationResponseDto
			.from(notificationRepository.save(Notification.of(userId, requestDto)));

		emitterService.sendToClient(userId, responseDto);

		return responseDto;
	}

	// 알림조회창에 들어갈 알림내역과 확인하지 않은 알림 개수 반환
	public AllNotificationResponseDto getNotifications(long userId) {
		return AllNotificationResponseDto.from(notificationRepository.findAllNotificationByUserId(userId)
			.stream().map(NotificationResponseDto::from).collect(Collectors.toList()));
	}


	// 일괄 확인 처리
	// 확인한 알림 중에서 가장 최신 알림의 생성일자를 받아와 해당 일자 이전인 알림만 확인 처리
	// 아니면 알림창을 닫을 때의 시간 데이터를 가져와서 처리
	public void checkedAllNotification(long userId, LocalDateTime time) {
		int checkedCount = notificationRepository.checkedAllNotification(userId, time);
		log.debug("확인 처리된 알림 개수 : {}", checkedCount);
	}
}
