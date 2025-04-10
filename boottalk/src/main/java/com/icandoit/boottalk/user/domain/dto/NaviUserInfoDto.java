package com.icandoit.boottalk.user.domain.dto;

import lombok.Builder;

@Builder
public record NaviUserInfoDto (
	String userName,
	Integer point
){
	public static NaviUserInfoDto from(String userName, Integer point) {
		return NaviUserInfoDto.builder()
			.userName(userName)
			.point(point).build();
	}
}
