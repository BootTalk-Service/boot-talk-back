package com.icandoit.boottalk.notification.controller;

import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.icandoit.boottalk.notification.service.SseEmitterService;
import com.icandoit.boottalk.social_login.dto.CustomOAuth2User;
import com.icandoit.boottalk.social_login.jwt.JwtProvider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class SseController {

	private final SseEmitterService sseEmitterService;
	private final JwtProvider jwtProvider;

	@GetMapping(value = "/sse-connect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public SseEmitter sseConnect (
		@AuthenticationPrincipal CustomOAuth2User oauth2User,
		@RequestHeader(value = "Last-Event-ID", required = false) String lastEventId) {

		// TODO : cookie 에서 userId 전송받기
		log.info("sseconnect : userId={}", oauth2User.getServiceUserId());
		return sseEmitterService.subscribe( oauth2User.getServiceUserId(), lastEventId);
	}
}
