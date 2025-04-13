package com.icandoit.boottalk.notification.controller;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.icandoit.boottalk.notification.dto.AllNotificationResponseDto;
import com.icandoit.boottalk.notification.dto.NotificationRequestDto;
import com.icandoit.boottalk.notification.service.NotificationService;
import com.icandoit.boottalk.notification.service.SseEmitterService;
import com.icandoit.boottalk.social_login.dto.CustomOAuth2User;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationController {

	private final NotificationService notificationService;
	private final SseEmitterService sseEmitterService;

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

	//TODO 추후 리펙토링 시 삭제
	@PostMapping("/test")
	public ResponseEntity<String> sendTestNotification(@RequestParam Long userId, @RequestBody NotificationRequestDto dto) {
		sseEmitterService.sendToClient(userId, dto);
		return ResponseEntity.ok("테스트용 알림을 보냈습니다.");
	}

}
