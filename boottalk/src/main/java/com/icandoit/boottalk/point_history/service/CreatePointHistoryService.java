package com.icandoit.boottalk.point_history.service;

import static com.icandoit.boottalk.libs.exception.ErrorCode.*;
import static com.icandoit.boottalk.point_history.domain.type.PointType.*;

import org.springframework.stereotype.Service;

import com.icandoit.boottalk.libs.exception.CustomException;
import com.icandoit.boottalk.point_history.domain.dto.PointHistoryDto;
import com.icandoit.boottalk.point_history.domain.entity.PointHistory;
import com.icandoit.boottalk.point_history.domain.repository.PointHistoryRepository;
import com.icandoit.boottalk.point_history.domain.type.EventType;
import com.icandoit.boottalk.point_history.domain.type.PointType;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CreatePointHistoryService {

	private final PointHistoryRepository pointHistoryRepository;


	public PointHistoryDto createPointHistory(EventType eventType, long userId, int changedPoint) {

		int currentPoint = getCurrentPoint(userId);

		//포인트 사용
		if (eventType.getPointType() == PointType.USED) {

			if (changedPoint > currentPoint) {
				throw new CustomException(INSUFFICIENT_POINT);
			}

			return PointHistoryDto.from(
				pointHistoryRepository.save(
					PointHistory.builder()
						.userId(userId)
						.currentPoint(currentPoint - changedPoint)
						.changedPoint(changedPoint)
						.pointType(USED)
						.eventType(eventType)
						.build()));
		}

		//포인트 적립
		return PointHistoryDto.from(
			pointHistoryRepository.save(
			PointHistory.builder()
				.userId(userId)
				.currentPoint(currentPoint + changedPoint)
				.changedPoint(changedPoint)
				.pointType(EARNED)
				.eventType(eventType)
				.build()));
	}

	private Integer getCurrentPoint(long userId) {

		PointHistory pointHistory = pointHistoryRepository.findTopByUserIdOrderByPointHistoryIdDesc(userId)
			.orElse(null);

		if(pointHistory == null) {
			return 0;
		}

		return pointHistory.getCurrentPoint();
	}
}
