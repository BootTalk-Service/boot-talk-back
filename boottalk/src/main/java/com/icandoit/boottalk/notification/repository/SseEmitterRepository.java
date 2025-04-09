package com.icandoit.boottalk.notification.repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Repository
@Slf4j
public class SseEmitterRepository {
	private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

	public Optional<SseEmitter> findById(long userId) {

		return Optional.ofNullable(emitters.get(userId));
	}

	public SseEmitter save(long userId ,SseEmitter sseEmitter) {

		// 모든 데이터가 전송되었다면 emitter 삭제
		sseEmitter.onCompletion(() -> {
			log.info("SSE 연결 종료 : 사용자 Id {}", userId);
			deleteById(userId);
		});

		//유효기간 만료시
		sseEmitter.onTimeout(() -> {
			log.debug("SSE 연결 시간 만료: 사용자 Id: {}", userId);
			deleteById(userId);});

		//클라이언트와 연결이 끊어졌을 때 동작
		sseEmitter.onError(e -> {
			log.warn("SSE 연결 오류: 사용자 Id: {}, 원인: {}", userId, e.getMessage());
			deleteById(userId);
		});


		emitters.put(userId, sseEmitter);
		return emitters.get(userId);
	}

	public void deleteById(long userId) {
		SseEmitter emitter = emitters.get(userId);
		if (emitter != null) {
			try {
				emitter.complete();
			} catch (Exception e) {
				log.warn("SSE 연결 종료 중 오류 발생 : 사용자 Id {}, 원인: {}", userId, e.getMessage());
			} finally {
				emitters.remove(userId);
			}
		}
	}
}
