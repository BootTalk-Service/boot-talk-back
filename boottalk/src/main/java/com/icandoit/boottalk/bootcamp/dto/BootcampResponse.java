package com.icandoit.boottalk.bootcamp.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.icandoit.boottalk.bootcamp.entity.Bootcamp;
import com.icandoit.boottalk.bootcamp.entity.BootcampCategoryType;

import lombok.Builder;
import lombok.Getter;

public class BootcampResponse {

	@Getter
	@Builder
	public static class BootcampResponseDto {

		private Long bootcampId;
		private String trainingCenterName;
		private String bootcampName;
		private String bootcampRegion;
		private boolean bootcampCost;
		private String bootcampLink;
		private String bootcampCategory;
		private int bootcampDegree;
		private int bootcampCapacity;
		private LocalDate bootcampStartDate;
		private LocalDate bootcampEndDate;

		private LocalDateTime createdAt;
		private LocalDateTime updatedAt;

		public static BootcampResponseDto from(Bootcamp bootcamp) {
			BootcampCategoryType bootcampCategoryType = bootcamp.getBootcampCategoryType();

			return BootcampResponseDto.builder()
				.bootcampId(bootcamp.getBootcampId())
				.trainingCenterName(bootcamp.getTrainingCenter().getTrainingCenterName())
				.bootcampName(bootcamp.getBootcampName())
				.bootcampRegion(bootcamp.getBootcampRegion())
				.bootcampCost(bootcamp.isBootcampCost())
				.bootcampLink(bootcamp.getBootcampLink())
				.bootcampCategory(bootcampCategoryType.getKoreanName())
				.bootcampDegree(bootcamp.getBootcampDegree())
				.bootcampCapacity(bootcamp.getBootcampCapacity())
				.bootcampStartDate(bootcamp.getBootcampStartDate())
				.bootcampEndDate(bootcamp.getBootcampEndDate())
				.createdAt(bootcamp.getCreatedAt())
				.updatedAt(bootcamp.getUpdatedAt())
				.build();
		}

		public static List<BootcampResponseDto> from(List<Bootcamp> bootcamps) {
			return bootcamps.stream()
				.map(BootcampResponseDto::from)
				.collect(Collectors.toList());
		}
	}
}
