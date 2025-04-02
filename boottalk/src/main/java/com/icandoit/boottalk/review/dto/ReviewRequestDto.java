package com.icandoit.boottalk.review.dto;

import jakarta.validation.constraints.NotNull;

public record ReviewRequestDto (
	Long reviewId,
	@NotNull(message = "훈련과정 ID는 필수 항목입니다.") String trainingProgramId,
	@NotNull(message = "내용은 필수 항목입니다.") String content,
	@NotNull(message = "평점은 필수 항목입니다.") int rating
) {}

