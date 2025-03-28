package com.icandoit.boottalk.point_history.domain.dto;

import java.time.LocalDateTime;

import com.icandoit.boottalk.point_history.domain.entity.PointHistory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PointHistoryDto {

	private long pointHistoryId;
	private long userId;
	private int currentPoint;
	private int changedPoint;
	private String pointTypeName;
	private String eventTypeName;
	private LocalDateTime createdAt;

	public static PointHistoryDto from(PointHistory point) {
		return PointHistoryDto.builder()
			.pointHistoryId(point.getPointHistoryId())
			.userId(point.getUserId())
			.currentPoint(point.getCurrentPoint())
			.changedPoint(point.getChangedPoint())
			.pointTypeName(point.getPointType().getTypeName())
			.eventTypeName(point.getEventType().getTypeName())
			.createdAt(point.getCreatedAt())
			.build();
	}
}
