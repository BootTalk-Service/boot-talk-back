package com.icandoit.boottalk.social_login.dto;

import com.icandoit.boottalk.user.domain.entity.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserAuthDto {

	private Long serviceUserId;
	private String resourceUserId;
	private UserRole role;

	public static UserAuthDto from(User user, UserRole role) {
		return UserAuthDto.builder()
			.serviceUserId(user.getUserId())
			.resourceUserId(user.getResourceUserId())
			.role(role)
			.build();
	}
}
