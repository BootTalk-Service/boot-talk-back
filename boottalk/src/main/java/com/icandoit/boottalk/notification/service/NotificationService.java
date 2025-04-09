package com.icandoit.boottalk.notification.service;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.icandoit.boottalk.notification.dto.NotificationRequestDto;
import com.icandoit.boottalk.notification.dto.NotificationResponseDto;
import com.icandoit.boottalk.notification.entity.Notification;
import com.icandoit.boottalk.notification.repository.NotificationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationService {

	private final NotificationRepository notificationRepository;
	private final SseEmitterService emitterService;


	// 알림을 저장하고 알림 대상자에게 전송
	public NotificationResponseDto sendNotification(long userId, NotificationRequestDto requestDto) {
		NotificationResponseDto responseDto = NotificationResponseDto
			.from(notificationRepository.save(Notification.of(userId, requestDto)));

		emitterService.sendToClient(userId, responseDto);

		return responseDto;
	}
}
