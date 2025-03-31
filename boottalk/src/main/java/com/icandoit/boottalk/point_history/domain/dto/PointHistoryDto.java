package com.icandoit.boottalk.point_history.domain.dto;

import java.time.LocalDateTime;

import com.icandoit.boottalk.point_history.domain.entity.PointHistory;

public record PointHistoryDto(
	long pointHistoryId,
	long userId,
	int currentPoint,
	int changedPoint,
	String pointTypeName,
	String eventTypeName,
	LocalDateTime createdAt
) {
	public static PointHistoryDto from(PointHistory pointHistory) {
		return new PointHistoryDto(
			pointHistory.getPointHistoryId(),
			pointHistory.getUserId(),
			pointHistory.getCurrentPoint(),
			pointHistory.getChangedPoint(),
			pointHistory.getPointType().getTypeName(),
			pointHistory.getEventType().getTypeName(),
			pointHistory.getCreatedAt()
		);
	}
}
