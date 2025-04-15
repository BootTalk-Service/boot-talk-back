package com.icandoit.boottalk.notification.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum NotificationType {
	COFFEE_CHAT_REQUEST_RECEIVED( "/coffee-chats/applications/received", "커피챗 신청이 들어왔습니다."),    // 멘티로부터 커피챗 신청 도착 알림
	COFFEE_CHAT_REQUEST_CANCELLED("/coffee-chats/applications/received","취소된 커피챗 신청이 있습니다."),    // 커피챗 신청 취소 알림
	COFFEE_CHAT_REQUEST_ACCEPTED("/coffee-chats", "신청한 커피챗이 수락되었습니다."),    // 멘토로부터 커피챗 신청 수락 알림
	COFFEE_CHAT_REQUEST_REJECTED("/coffee-chats", "신청한 커피챗이 거절되었습니다."),           // 커피챗 거절 알림
	CHAT_SUSPENDED("/coffee-chats/info", "커피챗 활동이 정지되었습니다."),		//등록된 커피챗 정지 알림
	CERTIFICATE_VERIFIED("/users/my", "수료증 인증이 완료되었습니다."),       // 수료증 인증 완료 알림
	CERTIFICATE_REJECTED("/users/my", "수료증 인증이 거절되었습니다."),// 수료증 인증 반려 알림
	NEW_BOOT_CAMP("/bootcamps", "관심있는 직군의 새로운 부트캠프가 등록되었습니다.");// 새로운 부트캠프 등록

	private final String urlFormat;
	private final String message;

}
