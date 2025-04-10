package com.icandoit.boottalk.bootcamp.dto;

import com.icandoit.boottalk.bootcamp.entity.BootcampCertification;
import com.icandoit.boottalk.bootcamp.entity.enums.BootcampCategoryType;

public record GetCertificationInfoDto(
	Long userId,
	String userName,
	Long certificationId,
	String fileUrl,
	String courseName,
	String trainingCenterName,
	BootcampCategoryType categoryType
) {
	public static GetCertificationInfoDto from(BootcampCertification certification) {
		return new GetCertificationInfoDto(
			certification.getUser().getUserId(),
			certification.getUser().getUserName(),
			certification.getId(),
			certification.getFileUrl(),
			certification.getCourse().getCourseName(),
			certification.getCourse().getTrainingCenter().getTrainingCenterName(),
			certification.getCourse().getBootcampCategoryType()
		);
	}
}
