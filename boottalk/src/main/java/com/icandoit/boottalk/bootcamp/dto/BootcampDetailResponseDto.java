package com.icandoit.boottalk.bootcamp.dto;

import java.time.LocalDate;

import com.icandoit.boottalk.bootcamp.entity.Bootcamp;

public record BootcampDetailResponseDto(
	Long bootcampId,
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
	Integer courseReviewCount,
	Long trainingCenterId,
	String trainingCenterName,
	String trainingCenterPhoneNumber,
	String trainingCenterEmail,
	String trainingCenterAddress,
	String trainingCenterUrl
) {
	public static BootcampDetailResponseDto from(
		Bootcamp bootcamp
	) {
		return new BootcampDetailResponseDto(
			bootcamp.getBootcampId(),
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
			bootcamp.getCourse().getReviewCount(),
			bootcamp.getTrainingCenter().getTrainingCenterId(),
			bootcamp.getTrainingCenter().getTrainingCenterName(),
			bootcamp.getTrainingCenter().getTrainingCenterPhoneNumber(),
			bootcamp.getTrainingCenter().getTrainingCenterEmail(),
			bootcamp.getTrainingCenter().getTrainingCenterAddress(),
			bootcamp.getTrainingCenter().getTrainingCenterUrl()
		);
	}
}
