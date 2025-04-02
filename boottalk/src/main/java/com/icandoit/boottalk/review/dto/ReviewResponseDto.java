package com.icandoit.boottalk.review.dto;

import java.time.LocalDateTime;

import com.icandoit.boottalk.review.entity.Review;

public record ReviewResponseDto(
	Long reviewId,
	Long bootcampId,
	String bootcampName,
	String userName,
	String content,
	int rating,
	LocalDateTime createdAt,
	LocalDateTime updatedAt
) {
	public static ReviewResponseDto from(Review review) {
		return new ReviewResponseDto(
			review.getReviewId(),
			review.getBootcamp().getBootcampId(),
			review.getBootcamp().getBootcampName(),
			review.getUser().getName(),
			review.getContent(),
			review.getRating(),
			review.getCreatedAt(),
			review.getUpdatedAt()
		);
	}
}


