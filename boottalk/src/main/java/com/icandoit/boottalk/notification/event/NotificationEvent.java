package com.icandoit.boottalk.notification.event;

import com.icandoit.boottalk.notification.dto.NotificationRequestDto;

public record NotificationEvent(
	Long userId,
	NotificationRequestDto notificationRequestDto
) {

}
