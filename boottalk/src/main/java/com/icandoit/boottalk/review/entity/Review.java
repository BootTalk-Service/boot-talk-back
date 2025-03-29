package com.icandoit.boottalk.review.entity;

import com.icandoit.boottalk.libs.entity.BaseEntity;
import com.icandoit.boottalk.review.dto.ReviewRequestDto;
import com.icandoit.boottalk.test.entity.Bootcamp;
import com.icandoit.boottalk.test.entity.User;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "review")
@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Review extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long reviewId;

	@ManyToOne
	@JoinColumn(name = "bootcamp_id")
	private Bootcamp bootcamp;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	private String content;
	private int rating;

	public static Review of(ReviewRequestDto dto, Bootcamp bootcamp, User user) {
		return Review.builder()
			.reviewId(dto.getReviewId())
			.bootcamp(bootcamp)
			.user(user)
			.content(dto.getContent())
			.rating(dto.getRating())
			.build();
	}

	public void update(ReviewRequestDto dto) {
		this.content = dto.getContent();
		this.rating = dto.getRating();
	}

}
