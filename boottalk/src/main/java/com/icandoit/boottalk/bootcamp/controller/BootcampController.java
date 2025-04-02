package com.icandoit.boottalk.bootcamp.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.icandoit.boottalk.bootcamp.dto.BootcampResponseDto;
import com.icandoit.boottalk.bootcamp.entity.Bootcamp;
import com.icandoit.boottalk.bootcamp.service.BootcampService;
import com.icandoit.boottalk.review.dto.ReviewResponseDto;
import com.icandoit.boottalk.review.service.ReviewService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/bootcamps")
@RequiredArgsConstructor
public class BootcampController {

	private final BootcampService bootcampService;
	private final ReviewService reviewService;

	// 부트캠프 목록 조회
	@GetMapping
	public ResponseEntity<List<BootcampResponseDto>> getAllBootcamps(
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size
	) {
		// TODO : 페이징 처리 수정, 테스트 코드 작성, 필터 조회
		Pageable pageable = PageRequest.of(page, size);
		Page<Bootcamp> bootcampPage = bootcampService.findAll(pageable);
		List<BootcampResponseDto> bootcampResponseDtoList = BootcampResponseDto.from(bootcampPage.getContent());

		return ResponseEntity.ok(bootcampResponseDtoList);
	}

	// 단일 부트캠프 조회
	@GetMapping("/{bootcampId}")
	public ResponseEntity<BootcampResponseDto> getBootcamp(@PathVariable Long bootcampId) {
		return ResponseEntity.ok(bootcampService.findById(bootcampId));
	}

	// 특정 부트캠프의 훈련 과정 (Course) 에 작성된 리뷰를 페이징으로 조회
	@GetMapping("/{bootcampId}/reviews")
	public ResponseEntity<Page<ReviewResponseDto>> getCourseReviews(
		@PathVariable Long bootcampId,
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size
	) {
		Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
		return ResponseEntity.ok(reviewService.getReviewsBootcampId(bootcampId, pageable));
	}
	// TODO : 검색 조회 (엔드 포인트 /search 진행 예정)
}
