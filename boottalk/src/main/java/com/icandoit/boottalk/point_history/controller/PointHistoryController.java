package com.icandoit.boottalk.point_history.controller;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.icandoit.boottalk.point_history.domain.dto.PointHistoryDto;
import com.icandoit.boottalk.point_history.domain.form.PointHistoryForm;
import com.icandoit.boottalk.point_history.domain.type.EventType;
import com.icandoit.boottalk.point_history.service.CreatePointHistoryService;
import com.icandoit.boottalk.point_history.service.SearchPointHistoryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/points/my")
public class PointHistoryController {

	private final SearchPointHistoryService searchPointHistoryService;
	private final CreatePointHistoryService createPointHistoryService;

	@GetMapping
	public ResponseEntity<List<PointHistoryDto>> getPointHistory(@RequestParam long userId,
		@PageableDefault(page = 0, size = 10, sort = "pointHistoryId", direction = Direction.DESC)
		Pageable pageable) {

		return ResponseEntity.ok(searchPointHistoryService.searchMyPointHistory(userId, pageable));
	}

	//테스트용
	@PostMapping
	public ResponseEntity<PointHistoryDto> createPointHistory(@RequestBody PointHistoryForm form) {

			return ResponseEntity.ok(createPointHistoryService.createPointHistory(
				form.getEventType(),
				form.getUserId(),
				form.getPoints()));

	}
}
