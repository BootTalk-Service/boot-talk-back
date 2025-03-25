package com.icandoit.boottalk.review.entity;

import com.icandoit.boottalk.entity.BaseEntity;
import com.icandoit.boottalk.test.entity.Bootcamp;
import com.icandoit.boottalk.test.entity.User;
import com.icandoit.boottalk.review.dto.ReviewRequestDto;
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
import lombok.Setter;

@Table(name = "review")
@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Review extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

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
			.id(dto.getId())
			.bootcamp(bootcamp)
			.user(user)
			.content(dto.getContent())
			.rating(dto.getRating())
			.build();
	}

}
