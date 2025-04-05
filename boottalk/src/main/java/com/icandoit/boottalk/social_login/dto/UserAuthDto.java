package com.icandoit.boottalk.social_login.dto;

import com.icandoit.boottalk.user.domain.entity.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Builder
public record UserAuthDto(
	Long serviceUserId,
	String resourceUserId,
	UserRole role
) {

	public static UserAuthDto from(User user, UserRole role) {
		return UserAuthDto.builder()
			.serviceUserId(user.getUserId())
			.resourceUserId(user.getResourceUserId())
			.role(role)
			.build();
	}
}
