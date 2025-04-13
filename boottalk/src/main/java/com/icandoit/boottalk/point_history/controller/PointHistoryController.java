package com.icandoit.boottalk.point_history.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.icandoit.boottalk.common.dto.PagedResponseDto;
import com.icandoit.boottalk.notification.service.SseEmitterService;
import com.icandoit.boottalk.point_history.domain.dto.PointHistoryDto;
import com.icandoit.boottalk.point_history.domain.form.PointHistoryForm;
import com.icandoit.boottalk.point_history.service.CreatePointHistoryService;
import com.icandoit.boottalk.point_history.service.SearchPointHistoryService;
import com.icandoit.boottalk.social_login.dto.CustomOAuth2User;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/points/my")
public class PointHistoryController {

	private final SearchPointHistoryService searchPointHistoryService;
	private final CreatePointHistoryService createPointHistoryService;


	@GetMapping
	public ResponseEntity<PagedResponseDto<PointHistoryDto>> getPointHistory(
		@AuthenticationPrincipal CustomOAuth2User user,
		@PageableDefault(page = 0, size = 10, sort = "pointHistoryId", direction = Direction.DESC)
		Pageable pageable) {

		return ResponseEntity.ok(searchPointHistoryService.searchMyPointHistory(user.getServiceUserId(), pageable));
	}

	//테스트용
	@PostMapping
	public ResponseEntity<PointHistoryDto> createPointHistory(@RequestBody PointHistoryForm form) {

		PointHistoryDto dto = createPointHistoryService.createPointHistory(
			form.getEventType(),
			form.getUserId(),
			form.getPoints());

		return ResponseEntity.ok(dto);

	}
}
