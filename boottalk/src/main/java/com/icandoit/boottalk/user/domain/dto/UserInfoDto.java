package com.icandoit.boottalk.user.domain.dto;

import java.util.List;

import com.icandoit.boottalk.bootcamp.dto.GetCertificationResponseDto;
import com.icandoit.boottalk.bootcamp.entity.enums.BootcampCategoryType;

public record UserInfoDto(
	Long userId,
	String name,
	String email,
	String profileImage,
	BootcampCategoryType desiredCareer,
	int currentPoint,
	List<GetCertificationResponseDto> certifications
) {
	public static UserInfoDto from(Long userId, UserDto userInfo, List<GetCertificationResponseDto> certifications, int curPoint) {
		return new UserInfoDto(
			userId,
			userInfo.name(),
			userInfo.email(),
			userInfo.profileImage(),
			userInfo.desiredCareer(),
			curPoint,
			certifications
		);
	}
}
