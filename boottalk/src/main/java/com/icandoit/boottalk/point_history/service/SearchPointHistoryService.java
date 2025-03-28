package com.icandoit.boottalk.point_history.service;

import com.icandoit.boottalk.point_history.domain.dto.PointHistoryDto;
import com.icandoit.boottalk.point_history.domain.repository.PointHistoryRepository;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SearchPointHistoryService {

	private final PointHistoryRepository pointHistoryRepository;

	public List<PointHistoryDto> searchMyPointHistory(long userId, Pageable pageable) {

		return pointHistoryRepository.findAllByUserId(userId, pageable)
			.getContent().stream()
			.map(PointHistoryDto::from)
			.toList();
	}
}
