package com.icandoit.boottalk.point_history.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.icandoit.boottalk.common.dto.PagedResponseDto;
import com.icandoit.boottalk.point_history.domain.dto.PointHistoryDto;
import com.icandoit.boottalk.point_history.service.CreatePointHistoryService;
import com.icandoit.boottalk.point_history.service.SearchPointHistoryService;
import com.icandoit.boottalk.social_login.dto.CustomOAuth2User;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/points/my")
public class PointHistoryController {

	private final SearchPointHistoryService searchPointHistoryService;


	@GetMapping
	public ResponseEntity<PagedResponseDto<PointHistoryDto>> getPointHistory(
		@AuthenticationPrincipal CustomOAuth2User user,
		@PageableDefault(page = 0, size = 10, sort = "pointHistoryId", direction = Direction.DESC)
		Pageable pageable) {

		return ResponseEntity.ok(searchPointHistoryService.searchMyPointHistory(user.getServiceUserId(), pageable));
	}
}
