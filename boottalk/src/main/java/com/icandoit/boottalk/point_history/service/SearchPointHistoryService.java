package com.icandoit.boottalk.point_history.service;

import com.icandoit.boottalk.common.dto.PagedResponseDto;
import com.icandoit.boottalk.point_history.domain.dto.PointHistoryDto;
import com.icandoit.boottalk.point_history.domain.entity.PointHistory;
import com.icandoit.boottalk.point_history.domain.repository.PointHistoryRepository;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SearchPointHistoryService {

	private final PointHistoryRepository pointHistoryRepository;

	@Transactional(readOnly = true)
	public PagedResponseDto<PointHistoryDto> searchMyPointHistory(long userId, Pageable pageable) {

		return PagedResponseDto.from(pointHistoryRepository.findAllByUserId(userId, pageable).map(PointHistoryDto::from));
	}
}
