package com.icandoit.boottalk.review.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.icandoit.boottalk.common.dto.PagedResponseDto;
import com.icandoit.boottalk.review.dto.ReviewCreateRequestDto;
import com.icandoit.boottalk.review.dto.ReviewResponseDto;
import com.icandoit.boottalk.review.dto.ReviewUpdateRequestDto;
import com.icandoit.boottalk.review.service.ReviewService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

	private final ReviewService reviewService;

	// 리뷰 등록
	@PostMapping
	public ResponseEntity<ReviewResponseDto> create(@RequestBody @Valid ReviewCreateRequestDto request) {
		Long userId = 1L; // test
		return ResponseEntity.ok(reviewService.create(request, userId));
	}

	// 전체 리뷰 목록 (정렬, 카테고리 필터링 포함)
	@GetMapping
	public ResponseEntity<PagedResponseDto<ReviewResponseDto>> listAll(
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size,
		@RequestParam(defaultValue = "latest") String sort,
		@RequestParam(required = false) String category
	) {
		Sort sortOption = sort.equals("oldest") ?
			Sort.by("createdAt").ascending() :
			Sort.by("createdAt").descending();

		Pageable pageable = PageRequest.of(page, size, sortOption);
		Page<ReviewResponseDto> reviewPage = reviewService.listAll(pageable, category);

		return ResponseEntity.ok(PagedResponseDto.from(reviewPage));
	}

	// 내 리뷰 목록
	@GetMapping("/my")
	public ResponseEntity<PagedResponseDto<ReviewResponseDto>> listMy(
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size
	) {
		Long userId = 1L; // 테스트용
		Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
		Page<ReviewResponseDto> reviewPage = reviewService.listMy(userId, pageable);

		return ResponseEntity.ok(PagedResponseDto.from(reviewPage));
	}

	// 내 리뷰 수정
	@PutMapping("/my/{reviewId}")
	public ResponseEntity<ReviewResponseDto> update(@PathVariable Long reviewId,
		@RequestBody @Valid ReviewUpdateRequestDto request) {
		Long userId = 1L; // test
		return ResponseEntity.ok(reviewService.update(request, reviewId, userId));

	}

	// 내 리뷰 삭제
	@DeleteMapping("/my/{reviewId}")
	public ResponseEntity<Void> delete(@PathVariable Long reviewId) {
		Long userId = 1L; // test
		reviewService.delete(reviewId, userId);
		return ResponseEntity.ok().build();
	}
}
