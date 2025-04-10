package com.icandoit.boottalk.user.domain.dto;

import java.util.List;

import com.icandoit.boottalk.bootcamp.dto.GetCertificationResponseDto;

public record UserInfoDto(
	UserDto userInfo,
	List<GetCertificationResponseDto> certifications
) {
	public static UserInfoDto from(UserDto userInfo, List<GetCertificationResponseDto> certifications) {
		return new UserInfoDto(userInfo, certifications);
	}
}
