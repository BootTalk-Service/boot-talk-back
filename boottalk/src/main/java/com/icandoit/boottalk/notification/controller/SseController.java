package com.icandoit.boottalk.notification.controller;

import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.icandoit.boottalk.notification.service.SseEmitterService;
import com.icandoit.boottalk.social_login.dto.CustomOAuth2User;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class SseController {

	private final SseEmitterService sseEmitterService;

	@PostMapping(value = "/sse-connect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public SseEmitter sseConnect (@AuthenticationPrincipal CustomOAuth2User user,
		@RequestHeader(value = "Last-Event-ID", required = false) String lastEventId) {

		return sseEmitterService.subscribe(user.getServiceUserId(), lastEventId);
	}
}
