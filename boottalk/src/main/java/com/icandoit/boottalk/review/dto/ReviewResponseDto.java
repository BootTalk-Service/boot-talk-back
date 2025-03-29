package com.icandoit.boottalk.review.dto;


import com.icandoit.boottalk.review.entity.Review;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ReviewResponseDto {

	private Long id;
	private Long bootcampId;
	private String bootcampName;
	private String userName;
	private String content;
	private int rating;

	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	public static ReviewResponseDto from(Review review) {
		return ReviewResponseDto.builder()
			.id(review.getReviewId())
			.bootcampId(review.getBootcamp().getId())
			.bootcampName(review.getBootcamp().getName())
			.userName(review.getUser().getName())
			.content(review.getContent())
			.rating(review.getRating())
			.createdAt(review.getCreatedAt())
			.updatedAt(review.getUpdatedAt())
			.build();
	}

}

