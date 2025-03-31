package com.icandoit.boottalk.review.entity;

import com.icandoit.boottalk.bootcamp.entity.Bootcamp;
import com.icandoit.boottalk.libs.entity.BaseEntity;
import com.icandoit.boottalk.review.dto.ReviewRequestDto;
import com.icandoit.boottalk.user.domain.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "review")
@Entity
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Review extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long reviewId;

	@ManyToOne
	@JoinColumn(name = "bootcamp_id", nullable = false)
	private Bootcamp bootcamp;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(nullable = false)
	private String content;
	@Column(nullable = false)
	private int rating;

	public static Review of(ReviewRequestDto dto, Bootcamp bootcamp, User user) {
		return Review.builder()
			.reviewId(dto.reviewId())
			.bootcamp(bootcamp)
			.user(user)
			.content(dto.content())
			.rating(dto.rating())
			.build();
	}

	public void update(ReviewRequestDto dto) {
		this.content = dto.content();
		this.rating = dto.rating();
	}

}
