package com.icandoit.boottalk.user_test.service;

import org.springframework.stereotype.Service;

import com.icandoit.boottalk.libs.exception.CustomException;
import com.icandoit.boottalk.libs.exception.ErrorCode;
import com.icandoit.boottalk.social_login.dto.CustomOAuth2User;
import com.icandoit.boottalk.social_login.dto.UserAuthDto;
import com.icandoit.boottalk.social_login.dto.UserRole;
import com.icandoit.boottalk.social_login.jwt.JwtProvider;
import com.icandoit.boottalk.user.domain.entity.User;
import com.icandoit.boottalk.user.domain.repository.UserRepository;
import com.icandoit.boottalk.user_test.form.TestSignUpForm;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserTestService {

	private final UserRepository userRepository;
	private final JwtProvider jwtProvider;

	public CustomOAuth2User signUp(TestSignUpForm form) {
		User user = userRepository.save(User.builder()
			.userName(form.userName())
			.email(form.email())
			.resourceUserId(form.resourceUserId())
			.desiredCareer(form.desiredCareer())
			.profileImage(form.profileImage())
			.build());

		return new CustomOAuth2User(UserAuthDto.from(user, UserRole.USER));
	}

	public String login(Long userId) {
		User user = userRepository.findById(userId).orElseThrow(
			() -> new CustomException(ErrorCode.USER_NOT_FOUND)
		);

		if (user.isAdmin()) {
			return jwtProvider.createToken(user.getUserId(), user.getUserName(), UserRole.ADMIN.name());
		}

		return jwtProvider.createToken(user.getUserId(), user.getUserName(), UserRole.USER.name());
	}
}
