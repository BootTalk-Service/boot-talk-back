package com.icandoit.boottalk.point_history.service;

import static com.icandoit.boottalk.libs.exception.ErrorCode.*;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.icandoit.boottalk.libs.exception.CustomException;
import com.icandoit.boottalk.notification.util.CreatePointEvent;
import com.icandoit.boottalk.point_history.domain.dto.PointHistoryDto;
import com.icandoit.boottalk.point_history.domain.entity.PointHistory;
import com.icandoit.boottalk.point_history.domain.repository.PointHistoryRepository;
import com.icandoit.boottalk.point_history.domain.type.EventType;
import com.icandoit.boottalk.point_history.domain.type.PointType;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreatePointHistoryService {

	private final PointHistoryRepository pointHistoryRepository;
	private final ApplicationEventPublisher eventPublisher;

	@Transactional
	public PointHistoryDto createPointHistory(EventType eventType, long userId, int changedPoint) {
		int currentPoint = getCurrentPoint(userId);

        log.info("eventType:{}, userId: {}, currentPoint: {}, changedPoint: {}", eventType, userId, currentPoint, changedPoint);

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

		PointHistory pointHistory = pointHistoryRepository.save(
			PointHistory.builder()
				.userId(userId)
				.currentPoint(currentPoint)
				.changedPoint(changedPoint)
				.pointType(eventType.getPointType())
				.eventType(eventType)
				.build());

		// 포인트 내역 생성 transaction 이 성공적으로 커밋되면 이벤트를 발생시켜 알림을 전송하도록 함.
		eventPublisher.publishEvent(new CreatePointEvent(userId));

		return PointHistoryDto.from(pointHistory);
	}

	// 포인트 내역을 생성할 때 사용 (이 떄는 락이 필요)
	private int getCurrentPoint(long userId) {

		PointHistory pointHistory = pointHistoryRepository.findTopByUserIdOrderByPointHistoryIdDesc(userId)
			.orElse(null);

		if(pointHistory == null) {
			return 0;
		}

		return pointHistory.getCurrentPoint();
	}

	// 내비게이션 상단 바에 표시될 포인트를 위한 메서드 (이 때는 굳이 학이 필요없음)
	public int getCurrentPointToNavi(long userId) {
		PointHistory pointHistory = pointHistoryRepository.findFirstByUserIdOrderByPointHistoryIdDesc(userId)
			.orElse(null);

		if(pointHistory == null) {
			return 0;
		}

		return pointHistory.getCurrentPoint();
	}
}
