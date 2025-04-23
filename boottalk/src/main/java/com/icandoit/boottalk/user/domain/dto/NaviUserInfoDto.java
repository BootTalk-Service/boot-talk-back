package com.icandoit.boottalk.user.domain.dto;

import lombok.Builder;

@Builder
public record NaviUserInfoDto (
	Long userId,
	String userName,
	Integer point
){
	public static NaviUserInfoDto from(Long userId, String userName, Integer point) {
		return NaviUserInfoDto.builder()
			.userId(userId)
			.userName(userName)
			.point(point).build();
	}
}
