package com.icandoit.boottalk.bootcamp.entity;

import java.time.LocalDate;

import com.icandoit.boottalk.libs.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "bootcamp")
public class Bootcamp extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false)
	private Long bootcampId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "training_center_id", nullable = false)
	private TrainingCenter trainingCenter;

	@Column(nullable = false)
	private String bootcampName;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private BootcampCategoryType bootcampCategoryType;

	@Column(nullable = false)
	private int bootcampDegree;

	@Column(nullable = false)
	private String bootcampRegion;

	@Column(nullable = false)
	private int bootcampCapacity;

	@Column(nullable = false)
	private boolean bootcampCost;

	@Column(nullable = false)
	private LocalDate bootcampStartDate;

	@Column(nullable = false)
	private LocalDate bootcampEndDate;

	@Column(nullable = false)
	private String bootcampLink;

	public static Bootcamp of(
		TrainingCenter trainingCenter, String name, BootcampCategoryType categoryType, int degree, String region,
		int capacity, boolean cost, LocalDate startDate, LocalDate endDate, String link
	) {
		return Bootcamp.builder()
			.trainingCenter(trainingCenter)
			.bootcampName(name)
			.bootcampCategoryType(categoryType)
			.bootcampDegree(degree)
			.bootcampRegion(region)
			.bootcampCapacity(capacity)
			.bootcampCost(cost)
			.bootcampStartDate(startDate)
			.bootcampEndDate(endDate)
			.bootcampLink(link)
			.build();
	}
}
