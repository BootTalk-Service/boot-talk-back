package com.icandoit.boottalk.social_login.dto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CustomOAuth2User implements OAuth2User {

	private final UserAuthDto userAuthDto;

	@Override
	public Map<String, Object> getAttributes() {
		return null;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {

		Collection<GrantedAuthority> authorities = new ArrayList<>();

		authorities.add(new GrantedAuthority() {

			@Override
			public String getAuthority() {
				return userAuthDto.role().name();
			}
		});

		return authorities;
	}

	@Override
	public String getName() {

		return userAuthDto.userName();
	}

	public Long getServiceUserId() {

		return userAuthDto.serviceUserId();
	}

}
