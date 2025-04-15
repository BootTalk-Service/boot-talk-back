package com.icandoit.boottalk.social_login.dto;

import java.util.Map;

import lombok.Builder;

@Builder
public record NaverResponse(
	String provider,
	String providerId,
	String email,
	String name
) {
	public static NaverResponse from(Map<String,Object> attributes) {
		return new NaverResponse(
			"naver",
			attributes.get("id").toString(),
			attributes.get("email").toString(),
			attributes.get("name").toString()
		);
	}

}
