package com.icandoit.boottalk.bootcamp.dto;

import java.time.LocalDateTime;

import com.icandoit.boottalk.bootcamp.entity.BootcampCertification;

public record CertificationResponseDto(
	String fileUrl,
	String courseName,
	String categoryName,
	String status,
	LocalDateTime createdAt
) {
	public static CertificationResponseDto from(BootcampCertification certification) {
		return new CertificationResponseDto(
			certification.getFileUrl(),
			certification.getCourse().getCourseName(),
			certification.getCourse().getBootcampCategoryType().getKoreanName(),     // 카테고리명 (Course 엔티티 내 카테고리 관련 메소드)
			certification.getStatus().name(),
			certification.getCreatedAt()
		);
	}
}
