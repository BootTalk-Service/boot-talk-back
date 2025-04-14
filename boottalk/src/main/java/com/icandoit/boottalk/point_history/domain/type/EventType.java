package com.icandoit.boottalk.point_history.domain.type;

import static com.icandoit.boottalk.point_history.domain.type.PointType.*;

import lombok.Getter;

@Getter
public enum EventType {
	SIGN_UP("회원가입", EARNED),
	COFFEE_CHAT_APPLY("커피챗 신청", USED),
	COFFEE_CHAT_RECEIVE("커피챗 수락", EARNED),
	REVIEW("리뷰 작성", EARNED),
	EVENT("이벤트", EARNED),
	REDEEM("환급", EARNED),
	COFFEE_CHAT_CANCEL_REFUND("커피챗 취소로 인한 환불", EARNED),
	COFFEE_CHAT_NO_RESPONSE_REFUND("커피챗 무응답으로 인한 환불", EARNED),
	REVIEW_DELETED("리뷰 삭제로 인한 포인트 회수", USED)
	;

	private final String typeName;
	private final PointType pointType;

	EventType(String typeName, PointType pointType) {
		this.typeName = typeName;
		this.pointType = pointType;
	}
}
