package com.icandoit.boottalk.notification.dto;

import com.icandoit.boottalk.notification.type.NotificationType;

import lombok.Builder;

@Builder
public record NotificationRequestDto(
	NotificationType type,
	Long targetId // 부트캠프 알림 전송인 경우 targetId에 부트캠프 Id 삽입.
) {
	public static NotificationRequestDto ofType(NotificationType type) {
		return new NotificationRequestDto(type, null);
	}
}
