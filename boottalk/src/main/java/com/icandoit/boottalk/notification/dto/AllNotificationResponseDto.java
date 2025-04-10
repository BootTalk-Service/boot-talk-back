package com.icandoit.boottalk.notification.dto;

import java.util.List;

import lombok.Builder;

@Builder
public record AllNotificationResponseDto(
	List<NotificationResponseDto> notificationResponseDtoList,
	int uncheckedCount
) {
	public static AllNotificationResponseDto from(
		List<NotificationResponseDto> notificationList) {
		int count = (int)notificationList.stream().filter(n -> !n.checked()).count();
		return AllNotificationResponseDto.builder()
			.notificationResponseDtoList(notificationList)
			.uncheckedCount(count).build();
	}
}
