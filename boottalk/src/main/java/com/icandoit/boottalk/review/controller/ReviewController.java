package com.icandoit.boottalk.review.controller;

import com.icandoit.boottalk.review.dto.ReviewRequestDto;
import com.icandoit.boottalk.review.dto.ReviewResponseDto;
import com.icandoit.boottalk.review.service.ReviewService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

	private final ReviewService reviewService;

	// 리뷰 등록
	@PostMapping
	public ResponseEntity<ReviewResponseDto> create(@RequestBody @Validated ReviewRequestDto request) {
		Long userId = 1L; // test
		return ResponseEntity.ok(reviewService.create(request, userId));
	}

	// 리뷰 목록 조회
	@GetMapping
	public ResponseEntity<List<ReviewResponseDto>> listAll() {
		// TODO : 페이징 처리
		return ResponseEntity.ok(reviewService.listAll());
	}

	// 내 리뷰 목록 조회
	@GetMapping("/my")
	public ResponseEntity<List<ReviewResponseDto>> listMy() {
		// TODO : 페이징 처리
		Long userId = 1L; // test
		return ResponseEntity.ok(reviewService.listMy(userId));
	}

	// 내 리뷰 수정
	@PutMapping("/my/{reviewId}")
	public ResponseEntity<ReviewResponseDto> update(@PathVariable Long reviewId,
													@RequestBody @Validated ReviewRequestDto request) {
		Long userId = 1L; // test
		return ResponseEntity.ok(reviewService.update(request, reviewId, userId));

	}

	// 내 리뷰 삭제
	@DeleteMapping("/my/{reviewId}")
	public void delete(@PathVariable Long reviewId) {
		Long userId = 1L; // test
		reviewService.delete(reviewId, userId);
	}

}
