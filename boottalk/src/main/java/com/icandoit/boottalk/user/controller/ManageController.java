package com.icandoit.boottalk.user.controller;

import com.icandoit.boottalk.common.dto.BaseResponse;
import com.icandoit.boottalk.user.domain.dto.UserDto;
import com.icandoit.boottalk.user.domain.entity.User;
import com.icandoit.boottalk.user.domain.form.UpdateForm;
import com.icandoit.boottalk.user.service.ManageService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/my")
public class ManageController {

	private final ManageService manageService;

	@GetMapping
	public ResponseEntity<UserDto> getUser(@RequestParam Long userId) {

		return ResponseEntity.ok(manageService.getUser(userId));
	}

	@PutMapping
	public ResponseEntity<UserDto> updateUser(@RequestParam Long userId,
		@RequestBody UpdateForm form) {

		return ResponseEntity.ok(manageService.updateUser(userId, form));
	}

	@DeleteMapping
	public ResponseEntity<String> deleteUser(@RequestParam Long userId) {

		manageService.deleteUser(userId);

		return ResponseEntity.ok("회원 탈퇴되었습니다.");
	}


}
