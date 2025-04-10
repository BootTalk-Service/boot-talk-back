package com.icandoit.boottalk.notification.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class DateTimeFormatterUtil {

	private final java.time.format.DateTimeFormatter dateTimeFormatter = java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSSSSS");

	public String formatTime(LocalDateTime localDateTime) {
		return localDateTime.format(dateTimeFormatter);
	}

	public LocalDateTime parseTime(String eventId) {
		try {
			return LocalDateTime.parse(eventId, dateTimeFormatter);
		} catch (DateTimeParseException e) {
			log.error("유효하지 않은 이벤트 ID: {}", eventId);
			return LocalDateTime.now().minusSeconds(10);
			// 파싱에러가 생겼을 때는 현재 시간 10초전을 기준으로 놓친알림을 탐색할 수 있도록 함.
		}
	}
}
