package com.icandoit.boottalk.bootcamp.dto;

import com.icandoit.boottalk.bootcamp.entity.BootcampCertification;

public record GetCertificationResponseDto(
	String courseName,
	String categoryName
) {
	public static GetCertificationResponseDto from(BootcampCertification certification){
		return new GetCertificationResponseDto(
			certification.getCourse().getCourseName(),
			certification.getCourse().getBootcampCategoryType().getKoreanName()
		);
	}
}
