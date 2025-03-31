package com.icandoit.boottalk.bootcamp.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
	LocalDateTime createdAt,
	LocalDateTime updatedAt
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
			bootcamp.getCreatedAt(),
			bootcamp.getUpdatedAt()
		);
	}

	public static List<BootcampResponseDto> from(List<Bootcamp> bootcamps) {
		return bootcamps.stream()
			.map(BootcampResponseDto::from)
			.collect(Collectors.toList());
	}
}

