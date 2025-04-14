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
			maskUserName(review.getUser().getUserName()),
			review.getContent(),
			review.getRating(),
			review.getCreatedAt(),
			review.getUpdatedAt()
		);
	}

	public static String maskUserName(String name) {
		if(name == null || name.isEmpty()) {
			return "";
		}

		int maskLength = name.length() - 1;
		return name.charAt(0) + "*".repeat(maskLength);
	}
}


