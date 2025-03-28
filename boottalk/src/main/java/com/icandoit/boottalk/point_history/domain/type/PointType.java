package com.icandoit.boottalk.point_history.domain.type;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum PointType {
	USED("포인트 사용"),
	EARNED("포인트 적립");
	private String typeName;
}
