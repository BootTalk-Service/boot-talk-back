package com.icandoit.boottalk.notification.type;

public enum NotificationType {
	COFFEE_CHAT_REQUEST_RECEIVED,    // 멘티로부터 커피챗 신청 도착 알림
	COFFEE_CHAT_REQUEST_ACCEPTED,    // 멘토로부터 커피챗 신청 수락 알림
	COFFEE_CHAT_REQUEST_REJECTED,           // 커피챗 거절 알림
	COFFEE_CHAT_REQUEST_CANCELLED,    // 커피챗 신청 취소 알림
	CHAT_SUSPENDED,		//등록된 커피챗 정지 알림
	CERTIFICATE_VERIFIED,       // 수료증 인증 완료 알림
	CERTIFICATE_REJECTED,// 수료증 인증 반려 알림
	BOOT_TALK_EVENT,
	BOOTCAMP_OPEN // 관심 직군의 bootcamp 오픈
}
