package com.icandoit.boottalk.user.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.icandoit.boottalk.bootcamp.dto.GetCertificationResponseDto;
import com.icandoit.boottalk.bootcamp.service.BootcampCertificationService;
import com.icandoit.boottalk.point_history.service.CreatePointHistoryService;
import com.icandoit.boottalk.social_login.dto.CustomOAuth2User;
import com.icandoit.boottalk.user.domain.dto.NaviUserInfoDto;
import com.icandoit.boottalk.user.domain.dto.UserDto;
import com.icandoit.boottalk.user.domain.dto.UserInfoDto;
import com.icandoit.boottalk.user.domain.form.UpdateForm;
import com.icandoit.boottalk.user.service.ManageService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/my")
public class ManageController {

	private final ManageService manageService;
	private final CreatePointHistoryService createPointHistoryService;
	private final BootcampCertificationService bootcampCertificationService;

	@GetMapping
	public ResponseEntity<UserInfoDto> getUser(@AuthenticationPrincipal CustomOAuth2User user) {
		Long userId = user.getServiceUserId();

		UserDto userDto = manageService.getUser(userId);
		List<GetCertificationResponseDto> myCertifications
			= bootcampCertificationService.getMyCertifications(userId);
		int currentPoint = createPointHistoryService.getCurrentPointToNavi(user.getServiceUserId());

		return ResponseEntity.ok(UserInfoDto.from(userDto, myCertifications, currentPoint));
	}

	@GetMapping("/navi")
	public ResponseEntity<NaviUserInfoDto> getNavi(@AuthenticationPrincipal CustomOAuth2User user) {
		return ResponseEntity.ok(NaviUserInfoDto.from(user.getName(),
			createPointHistoryService.getCurrentPointToNavi(user.getServiceUserId())));
	}

	@PutMapping
	public ResponseEntity<UserDto> updateUser(@AuthenticationPrincipal CustomOAuth2User user,
		@RequestBody UpdateForm form) {

		return ResponseEntity.ok(manageService.updateUser(user.getServiceUserId(), form));
	}

	@DeleteMapping
	public ResponseEntity<String> deleteUser(@AuthenticationPrincipal CustomOAuth2User user) {

		manageService.deleteUser(user.getServiceUserId());

		return ResponseEntity.ok("회원 탈퇴되었습니다.");
	}


}
