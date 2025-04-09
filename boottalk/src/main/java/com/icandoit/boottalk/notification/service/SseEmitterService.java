package com.icandoit.boottalk.notification.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.icandoit.boottalk.notification.dto.NotificationRequestDto;
import com.icandoit.boottalk.notification.dto.NotificationResponseDto;
import com.icandoit.boottalk.notification.repository.NotificationRepository;
import com.icandoit.boottalk.notification.repository.SseEmitterRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SseEmitterService {

	//SSE 이벤트 타임아웃
	private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;

	private final SseEmitterRepository emitterRepository;
	private final NotificationRepository notificationRepository;

	// 클라이언트와 연결을 생성하는 메서드
	public SseEmitter subscribe(long userId) {
		//기존 이미 연결이 되어있다면 끊고 다시 연결
		emitterRepository.deleteById(userId);

		// 유효 시간이 만료 시, 클라이언트에서 다시 서버로 이벤트 구독시도.
		SseEmitter sseEmitter = emitterRepository.save(userId, new SseEmitter(DEFAULT_TIMEOUT));

		// 첫 구독 시 이벤트 발생
		// 발생시키지 않고 하나의 데이터도 전송되지 않는다면 유효시간이 만료되고 503 에러 발생
		sendConnectEvent(userId, sseEmitter);

		return sseEmitter;
	}

	// 연결을 유지하기 위한 하트비트
	// TODO 주기적으로 동작할 수 있게끔
	private void sendHeartbeats() {
		emitterRepository.getEmitters().forEach((userId, emitter) -> {
			try {
				emitter.send(
					SseEmitter.event()
						.name("heartbeat")
						.data(""));
				log.debug("하트비트 전송 성공: 사용자 Id: {}", userId);
			} catch (IOException e) {
				log.error("하트비트 전송 실패: 사용자 Id: {}, 원인: {}", userId, e.getMessage());
			}
		});
	}

	// 현재 연결이 되어있는 사용자인 경우
	public void sendToClient(Long userId, NotificationResponseDto notification) {
		SseEmitter sseEmitter = emitterRepository.findById(userId)
			.orElse(null);
		//알림을 보낼 대상이 현재 연결이되지 않은 경우
		//TODO 추후에 대상이 다시 연결되었을 때 못받은 알림부터 받을 수 있도록 해야 함.
		if (sseEmitter == null) {
			log.info("알림 대상자 SSE 연결 비활성: 대상자 Id : {}", userId);
			return;
		}
		try {
			sseEmitter.send(
				SseEmitter.event()
					.id(userId.toString()) //TODO 잠깐 연결이 끊어졌을때 해당 Id를 기준으로 못받은 알림이 있는 지 확인.
					.name("notification") //이벤트 타입을 지정 클라이언트 측에서 해당 이벤트 타입에 따라 처리 가능
					.data(notification)
			);
		} catch (IOException e) {
			log.error("SSE 알림 전송 실패: 대상자 Id: {}, 원인: {}", userId, e.getMessage());
		}
	}

	private void sendConnectEvent(Long userId, SseEmitter emitter) {
		try {
			emitter.send(
				SseEmitter.event()
					.id("connect_" + userId)
					.name("connect")
					.data(Map.of(
						"message", "SSE 연결 성공",
						"userId", userId,
						"timestamp", LocalDateTime.now().toString()
					))
			);
			log.info("SSE 연결 성공: 사용자 Id: {}", userId);
		} catch (IOException e) {
			log.error("SSE 연결 실패: 사용자 Id: {}, 원인: {}", userId, e.getMessage());
		}
	}

}