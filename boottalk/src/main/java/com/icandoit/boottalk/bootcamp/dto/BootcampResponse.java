package com.icandoit.boottalk.bootcamp.dto;

import java.time.LocalDate;
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
		private Boolean bootcampCost;
		private String bootcampLink;
		private String bootcampCategory;
		private int bootcampDegree;
		private int bootcampCapacity;
		private LocalDate bootcampStartDate;
		private LocalDate bootcampEndDate;

		public static BootcampResponseDto from(Bootcamp bootcamp) {
			BootcampCategoryType bootcampCategoryType = bootcamp.getCategory();

			return BootcampResponseDto.builder()
				.bootcampId(bootcamp.getId())
				.trainingCenterName(bootcamp.getTrainingCenter().getName())
				.bootcampName(bootcamp.getName())
				.bootcampRegion(bootcamp.getRegion())
				.bootcampCost(bootcamp.isCost())
				.bootcampLink(bootcamp.getLink())
				.bootcampCategory(bootcampCategoryType.getKoreanNameByEnglishName(bootcampCategoryType.getKoreanName()))
				.bootcampDegree(bootcamp.getDegree())
				.bootcampCapacity(bootcamp.getCapacity())
				.bootcampStartDate(bootcamp.getStartDate())
				.bootcampEndDate(bootcamp.getEndDate())
				.build();
		}

		public static List<BootcampResponseDto> from(List<Bootcamp> bootcamps) {
			return bootcamps.stream()
				.map(BootcampResponseDto::from)
				.collect(Collectors.toList());
		}
	}
}
