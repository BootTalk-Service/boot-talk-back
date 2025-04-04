package com.icandoit.boottalk.social_login.oauth2;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

//
@Component
public class SocialClientRegistration {

	@Value("${spring.security.oauth2.registration.naver.client-id}")
	private String clientId;

	@Value("${spring.security.oauth2.registration.naver.client-secret}")
	private String clientSecret;


	//네이버 인증 서버에서 사용자로부터 권한 승인을 받으면 그로부터 인증 코드가 발급되고
	//해당 코드로 다시 엑세스토큰을 발급받아 네이버 api를 통해 네이버 사용자 정보를 가져올 수 있도록 함.
	@Bean
	public ClientRegistration naverClientRegistration() {

		return ClientRegistration.withRegistrationId("naver")
			.clientId(clientId)
			.clientSecret(clientSecret)
			.redirectUri("http://localhost:8080/login/oauth2/code/naver")
			.authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
			.scope("name", "email")
			.authorizationUri("https://nid.naver.com/oauth2.0/authorize")
			.tokenUri("https://nid.naver.com/oauth2.0/token")
			.userInfoUri("https://openapi.naver.com/v1/nid/me")
			.userNameAttributeName("response")
			.build();
	}
}
