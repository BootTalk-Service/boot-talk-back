package com.icandoit.boottalk.point_history.domain.type;

import lombok.Getter;

@Getter
public enum PointType {
	USED("포인트 사용"),
	EARNED("포인트 적립");
	private final String typeName;

	PointType(String typeName) {
		this.typeName = typeName;
	}
}
