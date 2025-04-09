package com.icandoit.boottalk.notification.dto;

import java.time.LocalDateTime;

import com.icandoit.boottalk.notification.entity.Notification;
import com.icandoit.boottalk.notification.type.NotificationType;

import lombok.Builder;

@Builder
public record NotificationRequestDto(
	NotificationType type,
	String message,
	String url
) {
}
