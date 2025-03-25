package com.icandoit.boottalk.review.controller;

import static com.icandoit.boottalk.common.type.SuccessCode.GET_ALL_REVIEWS_SUCCESS;
import static com.icandoit.boottalk.common.type.SuccessCode.GET_USER_REVIEWS_SUCCESS;
import static com.icandoit.boottalk.common.type.SuccessCode.REVIEW_DELETE_SUCCESS;
import static com.icandoit.boottalk.common.type.SuccessCode.REVIEW_REGISTER_SUCCESS;
import static com.icandoit.boottalk.common.type.SuccessCode.REVIEW_UPDATE_SUCCESS;

import com.icandoit.boottalk.common.dto.ApiResponse;
import com.icandoit.boottalk.review.dto.ReviewRequestDto;
import com.icandoit.boottalk.review.service.ReviewService;
import java.util.Collections;
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
	public ResponseEntity<ApiResponse> create(@RequestBody @Validated ReviewRequestDto request) {
		Long userId = 1L; // test
		return ResponseEntity.ok(ApiResponse.success(
			REVIEW_REGISTER_SUCCESS, reviewService.create(request, userId)));
	}

	// 리뷰 목록 조회
	@GetMapping
	public ResponseEntity<ApiResponse> listAll() {
		// TODO : 페이징 처리
		return ResponseEntity.ok(ApiResponse.success(
			GET_ALL_REVIEWS_SUCCESS, reviewService.listAll()));
	}

	// 내 리뷰 목록 조회
	@GetMapping("/my")
	public ResponseEntity<ApiResponse> listMy() {
		// TODO : 페이징 처리
		Long userId = 1L; // test
		return ResponseEntity.ok(ApiResponse.success(
			GET_USER_REVIEWS_SUCCESS, reviewService.listMy(userId)));
	}

	// 내 리뷰 수정
	@PutMapping("/my/{reviewId}")
	public ResponseEntity<ApiResponse> update(@PathVariable Long reviewId,
													@RequestBody @Validated ReviewRequestDto request) {
		Long userId = 1L; // test
		return ResponseEntity.ok(ApiResponse.success(
			REVIEW_UPDATE_SUCCESS, reviewService.update(request, reviewId, userId)));

	}

	// 내 리뷰 삭제
	@DeleteMapping("/my/{reviewId}")
	public ResponseEntity<ApiResponse> delete(@PathVariable Long reviewId) {
		Long userId = 1L; // test
		reviewService.delete(reviewId, userId);
		return ResponseEntity.ok(ApiResponse.success(
			REVIEW_DELETE_SUCCESS, Collections.emptyMap()));
	}

}
