package com.icandoit.boottalk.social_login.service;

import static com.icandoit.boottalk.social_login.dto.UserRole.*;

import java.util.Map;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.icandoit.boottalk.social_login.dto.CustomOAuth2User;
import com.icandoit.boottalk.social_login.dto.NaverResponse;
import com.icandoit.boottalk.social_login.dto.UserAuthDto;
import com.icandoit.boottalk.user.domain.entity.User;
import com.icandoit.boottalk.user.domain.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserService extends DefaultOAuth2UserService {

	private final UserRepository userRepository;

	// 네이버 리소스 서버로부터 받은 사용자 정보를 받아 사용자 인증 처리
	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

		OAuth2User oAuth2User = super.loadUser(userRequest);
		log.info("UserInfo from Service: {}", oAuth2User);

		String registrationId = userRequest.getClientRegistration().getRegistrationId();

		// 네이버의 경우 사용자 정보를 이중맵형식으로 전달받기 때문에 아래와 같이 작성
		NaverResponse naverResponse = NaverResponse.from((Map<String, Object>) oAuth2User.getAttributes().get("response"));

		// 기존 회원과 신규회원 구분할 id
		String resourceUserId = naverResponse.getProvider() + " " + naverResponse.getProviderId();

		// 삭제일자가 없는 기존회원 정보 가져오기
		User user = userRepository.findByResourceUserIdAndDeletedAtIsNull(resourceUserId).orElse(null);

		//신규회원인 경우, 회원가입(User 데이터 베이스에 저장)
		if (user == null) {
			return new CustomOAuth2User(UserAuthDto.from(
				userRepository.save(User.builder()
					.userName(naverResponse.getName())
					.email(naverResponse.getEmail())
					.resourceUserId(resourceUserId)
					.build())
				, NEW_USER
			));
		}

		// 기존 회원인 경우 인증 성공
		//TODO 회원 권한(관리자, 일반)을 구별할 수 있는 기능 필요
		return new CustomOAuth2User(UserAuthDto.from(user, USER));
	}


}
