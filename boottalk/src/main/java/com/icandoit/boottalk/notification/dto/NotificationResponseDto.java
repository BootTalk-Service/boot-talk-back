package com.icandoit.boottalk.notification.dto;

import java.time.LocalDateTime;

import com.icandoit.boottalk.notification.entity.Notification;
import com.icandoit.boottalk.notification.type.NotificationType;

import lombok.Builder;

@Builder
public record NotificationResponseDto(
	long notificationId,
	NotificationType type,
	String message,
	String url,
	boolean checked,
	LocalDateTime createdAt
) {
	public static NotificationResponseDto from(Notification notification) {
		return NotificationResponseDto.builder()
			.notificationId(notification.getNotificationId())
			.type(notification.getType())
			.message(notification.getMessage())
			.url(notification.getUrl())
			.checked(notification.isChecked())
			.createdAt(notification.getCreatedAt())
			.build();
	}
}
