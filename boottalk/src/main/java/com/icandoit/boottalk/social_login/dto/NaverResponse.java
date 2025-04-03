package com.icandoit.boottalk.social_login.dto;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class NaverResponse{

	private String provider;
	private String providerId;
	private String email;
	private String name;
	private String profileImage;

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
