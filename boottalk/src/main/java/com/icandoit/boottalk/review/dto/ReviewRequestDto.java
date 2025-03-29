package com.icandoit.boottalk.review.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ReviewRequestDto {

	private Long reviewId;
	@NotNull(message = "부트캠프 ID는 필수 항목입니다")
	private Long bootcampId;
	@NotNull(message = "내용은 필수 항목입니다")
	private String content;
	@NotNull(message = "평점은 필수 항목입니다")
	private int rating;

}

