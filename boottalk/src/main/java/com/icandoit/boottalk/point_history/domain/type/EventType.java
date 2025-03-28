package com.icandoit.boottalk.point_history.domain.type;

import static com.icandoit.boottalk.point_history.domain.type.PointType.EARNED;
import static com.icandoit.boottalk.point_history.domain.type.PointType.USED;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum EventType {
	SIGN_UP("회원가입", EARNED),
	COFFEE_CHAT_APPLY("커피챗 신청", USED),
	COFFEE_CHAT_RECEIVE("커피챗 수락", EARNED),
	REVIEW("리뷰 작성", EARNED),
	EVENT("이벤트", EARNED),
	REDEEM("환급", EARNED),;

	private String typeName;
	private PointType pointType;
}
