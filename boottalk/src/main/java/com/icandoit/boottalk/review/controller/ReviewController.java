package com.icandoit.boottalk.review.controller;

import java.util.Collections;

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

import com.icandoit.boottalk.libs.dto.SuccessResponseDto;
import com.icandoit.boottalk.review.dto.ReviewRequestDto;
import com.icandoit.boottalk.review.service.ReviewService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

	private final ReviewService reviewService;

	// 리뷰 등록
	@PostMapping
	public ResponseEntity<SuccessResponseDto> create(@RequestBody @Validated ReviewRequestDto request) {
		Long userId = 1L; // test
		return ResponseEntity.ok(SuccessResponseDto.of("리뷰 등록 완료", reviewService.create(request, userId)));
	}

	// 리뷰 목록 조회
	@GetMapping
	public ResponseEntity<SuccessResponseDto> listAll() {
		// TODO : 페이징 처리
		return ResponseEntity.ok(SuccessResponseDto.of("전체 리뷰 목록 조회 완료", reviewService.listAll()));
	}

	// 내 리뷰 목록 조회
	@GetMapping("/my")
	public ResponseEntity<SuccessResponseDto> listMy() {
		// TODO : 페이징 처리
		Long userId = 1L; // test
		return ResponseEntity.ok(SuccessResponseDto.of("사용자가 작성한 리뷰 조회 성공", reviewService.listMy(userId)));
	}

	// 내 리뷰 수정
	@PutMapping("/my/{reviewId}")
	public ResponseEntity<SuccessResponseDto> update(@PathVariable Long reviewId, @RequestBody @Validated ReviewRequestDto request) {
		Long userId = 1L; // test
		return ResponseEntity.ok(SuccessResponseDto.of("리뷰 수정 완료", reviewService.update(request, reviewId, userId)));

	}

	// 내 리뷰 삭제
	@DeleteMapping("/my/{reviewId}")
	public ResponseEntity<SuccessResponseDto> delete(@PathVariable Long reviewId) {
		Long userId = 1L; // test
		reviewService.delete(reviewId, userId);
		return ResponseEntity.ok(SuccessResponseDto.of("리뷰 삭제 성공", Collections.emptyMap()));
	}

}
