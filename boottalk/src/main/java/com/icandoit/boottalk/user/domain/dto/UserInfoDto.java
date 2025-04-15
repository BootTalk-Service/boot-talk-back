package com.icandoit.boottalk.user.domain.dto;

import java.util.List;

import com.icandoit.boottalk.bootcamp.dto.GetCertificationResponseDto;
import com.icandoit.boottalk.bootcamp.entity.enums.BootcampCategoryType;

public record UserInfoDto(
	String name,
	String email,
	String profileImage,
	BootcampCategoryType desiredCareer,
	int currentPoint,
	List<GetCertificationResponseDto> certifications
) {
	public static UserInfoDto from(UserDto userInfo, List<GetCertificationResponseDto> certifications, int curPoint) {
		return new UserInfoDto(
			userInfo.name(),
			userInfo.email(),
			userInfo.profileImage(),
			userInfo.desiredCareer(),
			curPoint,
			certifications
		);
	}
}
