package com.icandoit.boottalk.review.dto;

import jakarta.validation.constraints.NotNull;

public record ReviewRequestDto (
	Long reviewId,
	@NotNull(message = "부트캠프 ID는 필수 항목입니다.") Long bootcampId,
	@NotNull(message = "내용은 필수 항목입니다.") String content,
	@NotNull(message = "평점은 필수 항목입니다.") int rating
) {}

