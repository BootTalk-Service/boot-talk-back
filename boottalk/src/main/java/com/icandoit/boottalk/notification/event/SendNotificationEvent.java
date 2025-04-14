package com.icandoit.boottalk.notification.event;

import com.icandoit.boottalk.notification.dto.NotificationRequestDto;

public record SendNotificationEvent(
	Long userId,
	NotificationRequestDto notificationRequestDto
) {

}
