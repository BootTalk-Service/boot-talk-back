package com.icandoit.boottalk.social_login.dto;

import com.icandoit.boottalk.user.domain.entity.User;

import lombok.Builder;


@Builder
public record UserAuthDto(
	Long serviceUserId,
	String userName,
	UserRole role
) {

	public static UserAuthDto from(User user, UserRole role) {
		return UserAuthDto.builder()
			.serviceUserId(user.getUserId())
			.userName(user.getUserName())
			.role(role)
			.build();
	}
}
