package com.icandoit.boottalk.bootcamp.entity;

import com.icandoit.boottalk.libs.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "course")
public class Course extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long courseId;

	@Column(nullable = false, unique = true)
	private String trainingProgramId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "training_center_id")
	private TrainingCenter trainingCenter;

	@Column(nullable = false)
	private String courseName;

	private int totalScore;
	private int reviewCount;

	public void updateReviewStats(int totalScore, int reviewCount) {
		this.totalScore = totalScore;
		this.reviewCount = reviewCount;
	}

	public static Course of(String trainingProgramId, String courseName, TrainingCenter trainingCenter) {
		return Course.builder()
			.trainingProgramId(trainingProgramId)
			.courseName(courseName)
			.trainingCenter(trainingCenter)
			.build();
	}
}
