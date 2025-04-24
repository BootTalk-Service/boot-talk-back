package com.icandoit.boottalk.user.domain.dto;

import com.icandoit.boottalk.bootcamp.entity.enums.BootcampCategoryType;
import com.icandoit.boottalk.user.domain.entity.User;

import lombok.Builder;

@Builder
public record UserResponseDto(
	String name,
	String email,
	String profileImage,
	BootcampCategoryType desiredCareer
) {
	public static UserResponseDto from(User user) {
		return UserResponseDto.builder()
			.name(user.getUserName())
			.email(user.getEmail())
			.profileImage(user.getProfileImage())
			.desiredCareer(user.getDesiredCareer())
			.build();
	}
}
