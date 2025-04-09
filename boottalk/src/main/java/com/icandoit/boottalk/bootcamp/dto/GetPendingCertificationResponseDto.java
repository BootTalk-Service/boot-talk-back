package com.icandoit.boottalk.bootcamp.dto;

import com.icandoit.boottalk.bootcamp.entity.BootcampCertification;
import com.icandoit.boottalk.bootcamp.entity.enums.BootcampCategoryType;

public record GetPendingCertificationResponseDto(
	Long userId,
	String userName,
	Long CertificationId,
	BootcampCategoryType categoryType
) {
	public static GetPendingCertificationResponseDto from(BootcampCertification certification){
		return new GetPendingCertificationResponseDto(
			certification.getUser().getUserId(),
			certification.getUser().getUserName(),
			certification.getId(),
			certification.getCourse().getBootcampCategoryType()
		);
	}
}
