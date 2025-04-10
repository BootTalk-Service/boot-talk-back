package com.icandoit.boottalk.notification.controller;

import java.time.LocalDateTime;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.icandoit.boottalk.notification.dto.AllNotificationResponseDto;
import com.icandoit.boottalk.notification.service.NotificationService;
import com.icandoit.boottalk.social_login.dto.CustomOAuth2User;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationController {

	private final NotificationService notificationService;

	//sse 연결
	@PostMapping(value = "/sse-connect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public SseEmitter connect(@AuthenticationPrincipal CustomOAuth2User user,
		@RequestHeader(value = "Last-Event-ID", required = false) String lastEventId) {

		return notificationService.connect(user.getServiceUserId(), lastEventId);
	}

	//알림 조회창 열었을 때 사용자 알림 반환
	@GetMapping
	public ResponseEntity<AllNotificationResponseDto> getNotifications(
		@AuthenticationPrincipal CustomOAuth2User user
	) {
		return ResponseEntity.ok(notificationService.getNotifications(user.getServiceUserId()));
	}

	//알림 조회창이 닫힐 시에 조회된 모든 알림들을 확인 처리함.
	@PatchMapping
	public ResponseEntity<String> checkedNotification(
		@AuthenticationPrincipal CustomOAuth2User user,
		@RequestParam LocalDateTime time) {
		notificationService.checkedAllNotification(user.getServiceUserId(), time);
		return ResponseEntity.ok("알림을 확인하였습니다.");
	}

}
