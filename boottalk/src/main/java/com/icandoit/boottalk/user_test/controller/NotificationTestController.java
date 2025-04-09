package com.icandoit.boottalk.user_test.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.icandoit.boottalk.notification.dto.NotificationRequestDto;
import com.icandoit.boottalk.notification.dto.NotificationResponseDto;
import com.icandoit.boottalk.notification.service.NotificationService;
import com.icandoit.boottalk.notification.service.SseEmitterService;
import com.icandoit.boottalk.social_login.jwt.JwtProvider;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/test/notification")
@RequiredArgsConstructor
public class NotificationTestController {

	private final NotificationService notificationService;
	private final SseEmitterService emitterService;

	@PostMapping("/sse-endpoint")
	public ResponseEntity<SseEmitter> connect(@RequestParam long userId) {
		return ResponseEntity.ok(emitterService.subscribe(userId));
	}


	@PostMapping
	public ResponseEntity<NotificationResponseDto>  create(@RequestParam long userId, @RequestBody NotificationRequestDto dto) {
		return ResponseEntity.ok(notificationService.sendNotification(userId, dto));
	}
}
