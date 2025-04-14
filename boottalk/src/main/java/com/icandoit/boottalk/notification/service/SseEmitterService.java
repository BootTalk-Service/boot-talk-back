package com.icandoit.boottalk.notification.service;

import static com.icandoit.boottalk.libs.exception.ErrorCode.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.icandoit.boottalk.libs.exception.CustomException;
import com.icandoit.boottalk.notification.dto.NotificationRequestDto;
import com.icandoit.boottalk.notification.dto.NotificationResponseDto;
import com.icandoit.boottalk.notification.entity.Notification;
import com.icandoit.boottalk.notification.event.CreatePointEvent;
import com.icandoit.boottalk.notification.event.SendNotificationEvent;
import com.icandoit.boottalk.notification.repository.NotificationRepository;
import com.icandoit.boottalk.notification.repository.SseEmitterRepository;
import com.icandoit.boottalk.notification.util.DateTimeFormatterUtil;
import com.icandoit.boottalk.point_history.service.CreatePointHistoryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SseEmitterService {
	//SSE 이벤트 타임아웃
	private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;
	//SSE eventId를 LocalDateTime 으로 설정하기 위한 포맷
	private final DateTimeFormatterUtil dateTimeFormatter;



	private final SseEmitterRepository emitterRepository;
	private final NotificationRepository notificationRepository;
	private final CreatePointHistoryService createPointHistoryService;


	// 클라이언트와 연결을 생성하는 메서드
	public SseEmitter subscribe(long userId, String lastEventId) {
		//기존 이미 연결이 되어있다면 끊고 다시 연결
		emitterRepository.deleteById(userId);

		SseEmitter sseEmitter = emitterRepository.save(userId, new SseEmitter(DEFAULT_TIMEOUT));

		// 첫 구독 시 이벤트 발생
		// 발생시키지 않고 하나의 데이터도 전송되지 않는다면 유효시간이 만료되고 503 에러 발생하기 때문
		sendConnectEvent(userId, sseEmitter);

		// 비정상적으로 연결이 종료되었을 때 놓친 알림들이 있다면 다시 전송
		if (lastEventId != null) {
			List<Notification> notifications = notificationRepository.findByMissedNotifications(
				userId, dateTimeFormatter.parseTime(lastEventId));
			if (!notifications.isEmpty()) {
				for (Notification notification : notifications) {
					try {
						sseEmitter.send(
							SseEmitter.event()
								.id(dateTimeFormatter.formatTime(notification.getCreatedAt()))
								.name("notification")
								.data(NotificationResponseDto.from(notification))
						);
					} catch (IOException e) {
						log.error("SSE 알림 전송 실패: 대상자 Id: {}, 원인: {}", userId, e.getMessage());
						throw new CustomException(SSE_CONNECTION_FAILED);
					}
				}
			}
		}

		// 연결되었을 때 현재 포인트 데이터를 가져옴
		sendPointNotification(userId);

		return sseEmitter;
	}

	@Async
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void createPointEvent(CreatePointEvent createPointEvent) {
		sendPointNotification(createPointEvent.userId());
	}

	@Async
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void sendNotificationEvent(SendNotificationEvent sendNotificationEvent) {
		sendToClient(sendNotificationEvent.userId(), sendNotificationEvent.notificationRequestDto());
	}

	// 이벤트를 발행한 트랜잭션이 성공적으로 커밋된 후에만 해당 이벤트 핸들러가 실행됨.
	// 알림 전송
	private void sendToClient(Long userId, NotificationRequestDto requestDto) {
		// 먼저 알림을 보내기 전 알림 저장
		NotificationResponseDto responseDto = NotificationResponseDto
			.from(notificationRepository.save(Notification.of(userId, requestDto)));

		SseEmitter sseEmitter = findById(userId);
		if (sseEmitter == null) {
			log.info("알림 대상자 SSE 연결 비활성: 대상자 Id : {}", userId);
			return;
		}
		try {
			sseEmitter.send(
				SseEmitter.event()
					.id(dateTimeFormatter.formatTime(responseDto.createdAt())) //잠깐 연결이 끊어졌을때 해당 Id를 기준으로 못받은 알림이 있는 지 확인.
					.name("notification") //이벤트 타입을 지정 클라이언트 측에서 해당 이벤트 타입에 따라 처리 가능
					.data(responseDto)
			);
		} catch (IOException e) {
			log.error("SSE 알림 전송 실패: 대상자 Id: {}, 원인: {}", userId, e.getMessage());
			throw new CustomException(SSE_CONNECTION_FAILED);
		}
	}

	// 포인트 변동사항 발생 시 상단 내비 바에 있는 포인트에 실시간으로 반영될 수 있도록 알림 전송
	private void sendPointNotification(Long userId) {

		SseEmitter sseEmitter = findById(userId);
		int point = createPointHistoryService.getCurrentPointToNavi(userId);
		if (sseEmitter == null) {
			log.info("알림 대상자 SSE 연결 비활성: 대상자 Id : {}", userId);
			return;
		}
		try {
			sseEmitter.send(
				SseEmitter.event()
					.id(dateTimeFormatter.formatTime(LocalDateTime.now()))
					.name("point")
					.data(point)
			);
		} catch (IOException e) {
			log.error("SSE 알림 전송 실패: 대상자 Id: {}, 원인: {}", userId, e.getMessage());
			throw new CustomException(SSE_CONNECTION_FAILED);
		}
	}

	// TODO 로그아웃 되었을 때 해당 메서드를 통해 명시적 연결 종료
	public void unsubscribe(Long userId) {
		log.info("SSE 연결 종료 : 대상자 Id: {}", userId);
		emitterRepository.deleteById(userId);
	}


	// 연결되었을 때 연결 성공 메시지를 보냄
	private void sendConnectEvent(Long userId, SseEmitter emitter) {
		try {

			emitter.send(
				SseEmitter.event()
					.id(dateTimeFormatter.formatTime(LocalDateTime.now()))
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
			throw new CustomException(SSE_CONNECTION_FAILED);
		}
	}

	// 클라이언트들의 연결을 유지하기 위한 하트비트 1분마다 전송
	@Scheduled(fixedRate = 60 * 1000)
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
				throw new CustomException(SSE_CONNECTION_FAILED);
			}
		});
	}

	private SseEmitter findById(Long userId) {
		//알림을 보낼 대상이 현재 연결이되지 않은 경우

		return emitterRepository.findById(userId)
			.orElse(null);
	}
}