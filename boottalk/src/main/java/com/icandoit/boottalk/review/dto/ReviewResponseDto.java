package com.icandoit.boottalk.review.dto;

import java.time.LocalDateTime;

import com.icandoit.boottalk.review.entity.Review;

public record ReviewResponseDto(
	Long reviewId,
	String trainingProgramId,
	String courseName,
	String userName,
	String content,
	int rating,
	LocalDateTime createdAt,
	LocalDateTime updatedAt
) {
	public static ReviewResponseDto from(Review review) {
		return new ReviewResponseDto(
			review.getReviewId(),
			review.getCourse().getTrainingProgramId(),
			review.getCourse().getCourseName(),
			review.getUser().getName(),
			review.getContent(),
			review.getRating(),
			review.getCreatedAt(),
			review.getUpdatedAt()
		);
	}
}


