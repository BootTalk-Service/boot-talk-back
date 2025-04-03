package com.icandoit.boottalk.social_login.oauth2;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;

// SocialClientRegistration 정보들을 저장할 장소
@Configuration
public class CustomClientRegistrationRepository {

	private final SocialClientRegistration socialClientRegistration;

	public CustomClientRegistrationRepository(SocialClientRegistration socialClientRegistration) {

		this.socialClientRegistration = socialClientRegistration;
	}

	public ClientRegistrationRepository clientRegistrationRepository() {

		return new InMemoryClientRegistrationRepository(socialClientRegistration.naverClientRegistration());

	}
}
