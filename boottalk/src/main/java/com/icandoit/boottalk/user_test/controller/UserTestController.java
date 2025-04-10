package com.icandoit.boottalk.user_test.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.icandoit.boottalk.notification.service.SseEmitterService;
import com.icandoit.boottalk.social_login.dto.CustomOAuth2User;
import com.icandoit.boottalk.social_login.dto.UserRole;
import com.icandoit.boottalk.social_login.jwt.JwtProvider;
import com.icandoit.boottalk.user_test.form.TestSignUpForm;
import com.icandoit.boottalk.user_test.service.UserTestService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/test/users")
public class UserTestController {

	private final UserTestService userTestService;
	private final JwtProvider jwtProvider;

	//테스트 회원 가입
	@PostMapping("/signup")
	public ResponseEntity<String> signUp(@RequestBody TestSignUpForm form) {
		CustomOAuth2User authUser = userTestService.signUp(form);

		return ResponseEntity.ok(
			jwtProvider.createToken(authUser.getServiceUserId(), authUser.getName(), UserRole.USER.name()));
	}


	//로그인 (회원가입된 이름으로 로그인)
	@GetMapping("/login")
	public ResponseEntity<String> login(@RequestParam String username) {
		CustomOAuth2User authUser = userTestService.login(username);

		return ResponseEntity.ok(
			jwtProvider.createToken(authUser.getServiceUserId(), authUser.getName(), UserRole.USER.name()));

	}


}
