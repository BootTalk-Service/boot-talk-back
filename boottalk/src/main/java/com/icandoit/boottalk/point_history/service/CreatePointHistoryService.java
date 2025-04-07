package com.icandoit.boottalk.point_history.service;

import static com.icandoit.boottalk.libs.exception.ErrorCode.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

	@Transactional
	public PointHistoryDto createPointHistory(EventType eventType, long userId, int changedPoint) {

		int currentPoint = getCurrentPoint(userId);

		//포인트 사용
		if (eventType.getPointType() == PointType.USED) {
			if (changedPoint > currentPoint) {
				throw new CustomException(INSUFFICIENT_POINT);
			}

			currentPoint -= changedPoint;

		} else {
		//포인트 적립
			currentPoint += changedPoint;
		}

		return PointHistoryDto.from(
			pointHistoryRepository.save(
			PointHistory.builder()
				.userId(userId)
				.currentPoint(currentPoint)
				.changedPoint(changedPoint)
				.pointType(eventType.getPointType())
				.eventType(eventType)
				.build()));
	}

	public int getCurrentPoint(long userId) {

		PointHistory pointHistory = pointHistoryRepository.findTopByUserIdOrderByPointHistoryIdDesc(userId)
			.orElse(null);

		if(pointHistory == null) {
			return 0;
		}

		return pointHistory.getCurrentPoint();
	}
}
