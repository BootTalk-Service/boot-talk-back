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

import com.icandoit.boottalk.bootcamp.dto.BootcampAutocompleteDto;
import com.icandoit.boottalk.bootcamp.dto.BootcampResponseDto;
import com.icandoit.boottalk.bootcamp.entity.Bootcamp;
import com.icandoit.boottalk.bootcamp.entity.enums.BootcampCategoryType;
import com.icandoit.boottalk.bootcamp.repository.BootcampRepository;
import com.icandoit.boottalk.bootcamp.service.BootcampService;
import com.icandoit.boottalk.common.dto.PagedResponseDto;
import com.icandoit.boottalk.review.dto.ReviewResponseDto;
import com.icandoit.boottalk.review.service.ReviewService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/bootcamps")
@RequiredArgsConstructor
public class BootcampController {

	private final BootcampService bootcampService;
	private final ReviewService reviewService;
	private final BootcampRepository bootcampRepository;

	// 지역별(시), 카테고리별(BootcampCategory), 평균 평점 별(없음, 1, 2, 3, 4 이상), 기간별 (4주 미만, 4 ~ 12주, 12 주 이상)
	// 전체 조회 or 필터 기반 조회 (키워드 없음)
	@GetMapping
	public ResponseEntity<PagedResponseDto<BootcampResponseDto>> getBootcamps(
		@RequestParam(required = false) String region,
		@RequestParam(required = false) String category,
		@RequestParam(required = false) Integer minRating,
		@RequestParam(required = false) Integer duration,
		Pageable pageable
	) {
		BootcampCategoryType categoryEnum = null;

		if (category != null) {
			categoryEnum = BootcampCategoryType.fromKoreanName(category);
		}

		Page<BootcampResponseDto> result = bootcampService.searchBootcamps(
			region, categoryEnum, minRating, duration, null, null, pageable
		);
		return ResponseEntity.ok(PagedResponseDto.from(result));
	}

	// 검색 기반 조회 (키워드 + 필터 + 정렬 포함)
	@GetMapping("/search")
	public ResponseEntity<PagedResponseDto<BootcampResponseDto>> searchBootcamps(
		@RequestParam(required = false) String region,
		@RequestParam(required = false) BootcampCategoryType category,
		@RequestParam(required = false) Integer minRating,
		@RequestParam(required = false) Integer duration,
		@RequestParam(required = false) String keyword,
		@RequestParam(defaultValue = "latest") String sort,
		Pageable pageable
	) {
		BootcampCategoryType categoryEnum = null;

		if (category != null) {
			categoryEnum = BootcampCategoryType.fromKoreanName(category.name());
		}

		Page<BootcampResponseDto> result = bootcampService.searchBootcamps(
			region, categoryEnum, minRating, duration, keyword, sort, pageable
		);
		return ResponseEntity.ok(PagedResponseDto.from(result));
	}

	// 단일 부트캠프 조회
	@GetMapping("/{bootcampId}")
	public ResponseEntity<BootcampResponseDto> getBootcamp(@PathVariable Long bootcampId) {
		return ResponseEntity.ok(bootcampService.findById(bootcampId));
	}

	// 특정 부트캠프의 훈련 과정 (Course) 에 작성된 리뷰를 페이징으로 조회
	@GetMapping("/{bootcampId}/reviews")
	public ResponseEntity<PagedResponseDto<ReviewResponseDto>> getCourseReviews(
		@PathVariable Long bootcampId,
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size
	) {
		Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
		Page<ReviewResponseDto> result = reviewService.getReviewsBootcampId(bootcampId, pageable);

		return ResponseEntity.ok(PagedResponseDto.from(result));
	}

	@GetMapping("/autocomplete")
	public ResponseEntity<List<BootcampAutocompleteDto>> autocomplete(
		@RequestParam("query") String query
	) {
		Pageable pageable = PageRequest.of(0, 6);
		List<Bootcamp> bootcamps = bootcampRepository.findByBootcampNameContainingIgnoreCase(query, pageable);
		List<BootcampAutocompleteDto> result = bootcamps.stream()
			.map(bootcamp -> new BootcampAutocompleteDto(bootcamp.getBootcampId(), bootcamp.getBootcampName()))
			.toList();
		return ResponseEntity.ok(result);
	}
}
