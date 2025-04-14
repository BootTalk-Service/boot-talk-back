package com.icandoit.boottalk.bootcamp.dto;

import java.time.LocalDate;
import java.util.List;

import com.icandoit.boottalk.bootcamp.entity.Bootcamp;

public record BootcampResponseDto(
	Long bootcampId,
	String trainingCenterName,
	String bootcampName,
	String bootcampRegion,
	boolean bootcampCost,
	String bootcampLink,
	String bootcampCategory,
	int bootcampDegree,
	int bootcampCapacity,
	LocalDate bootcampStartDate,
	LocalDate bootcampEndDate,
	Double courseAverageRating,
	Integer courseReviewCount
) {
	public static BootcampResponseDto from(Bootcamp bootcamp) {
		return new BootcampResponseDto(
			bootcamp.getBootcampId(),
			bootcamp.getTrainingCenter().getTrainingCenterName(),
			bootcamp.getBootcampName(),
			bootcamp.getBootcampRegion(),
			bootcamp.isBootcampCost(),
			bootcamp.getBootcampLink(),
			bootcamp.getBootcampCategoryType().getKoreanName(),
			bootcamp.getBootcampDegree(),
			bootcamp.getBootcampCapacity(),
			bootcamp.getBootcampStartDate(),
			bootcamp.getBootcampEndDate(),
			bootcamp.getCourse().getAverageRating(),
			bootcamp.getCourse().getReviewCount()
		);
	}

	public static List<BootcampResponseDto> from(List<Bootcamp> bootcamps) {
		return bootcamps.stream()
			.map(BootcampResponseDto::from)
			.toList();
	}
}
