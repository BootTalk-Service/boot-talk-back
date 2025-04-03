package com.icandoit.boottalk.review.dto;

import jakarta.validation.constraints.NotNull;

public record ReviewUpdateRequestDto(
	@NotNull(message = "내용은 필수 항목입니다.") String content,
	@NotNull(message = "평점은 필수 항목입니다.") int rating
) {
}
