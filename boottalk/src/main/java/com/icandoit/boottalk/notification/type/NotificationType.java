package com.icandoit.boottalk.notification.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum NotificationType {

	COFFEE_CHAT_REQUEST_RECEIVED( "/coffee-chat/my/received", "커피챗 신청이 들어왔습니다."),    // 멘티로부터 커피챗 신청 도착 알림
	COFFEE_CHAT_REQUEST_CANCELLED_FROM_MENTEE("/coffee-chat/my/received","취소된 커피챗 신청이 있습니다."),    // 커피챗 신청 취소 알림
	COFFEE_CHAT_REQUEST_APPROVED("/coffee-chat/my/sent", "신청한 커피챗이 수락되었습니다."),    // 멘토로부터 커피챗 신청 수락 알림
	COFFEE_CHAT_REQUEST_REJECTED("/coffee-chat/my/sent", "신청한 커피챗이 거절되었습니다."),           // 커피챗 거절 알림
	COFFEE_CHAT_REQUEST_CANCELLED_FROM_MENTOR("/coffee-chat/my/sent","신청한 커피챗이 취소되었습니다."),    // 커피챗 취소 알림
	CHAT_SUSPENDED("", "커피챗 활동이 정지되었습니다."),		//등록된 커피챗 정지 알림
	COFFEE_CHAT_AUTO_REFUND("/coffee-chat/my/sent", "신청한 커피챗이 만료되어 자동 환불되었습니다."),	// 커피챗 자동 환불 알림
	CERTIFICATE_VERIFIED("/mypage?tab=profile", "수료증 인증이 완료되었습니다."),       // 수료증 인증 완료 알림
	CERTIFICATE_REJECTED("/mypage?tab=certificates", "수료증 인증이 거절되었습니다."),// 수료증 인증 반려 알림
	NEW_BOOT_CAMP("/bootcamps", "관심있는 직군의 새로운 부트캠프가 등록되었습니다."),// 새로운 부트캠프 등록
    COFFEE_CHAT_REMINDER_30_MINUTES_AHEAD("/chat-rooms", "곧 커피챗이 시작됩니다. 늦지 않게 준비하시기 바랍니다!"),
	CHAT_MESSAGE_RECEIVED("/chat", "상대방이 메시지를 보냈어요. 커피챗을 이어가볼까요? ☕");

	private final String urlFormat;
	private final String message;

}
