package com.icandoit.boottalk.social_login.dto;

import java.util.Map;

import lombok.Builder;

@Builder
public record NaverResponse(
	String provider,
	String providerId,
	String email,
	String name,
	String profileImage
) {
	public static NaverResponse from(Map<String,Object> attributes) {
		return NaverResponse.builder()
			.provider("naver")
			.providerId(attributes.get("id").toString())
			.email(attributes.get("email").toString())
			.name(attributes.get("name").toString())
			.profileImage(attributes.get("profile_image").toString())
			.build();
	}

}
