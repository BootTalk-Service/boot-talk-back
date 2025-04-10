package com.icandoit.boottalk.bootcamp.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.icandoit.boottalk.bootcamp.dto.CertificationCreationRequestDto;
import com.icandoit.boottalk.bootcamp.dto.CertificationResponseDto;
import com.icandoit.boottalk.bootcamp.dto.CertificationUpdateRequestDto;
import com.icandoit.boottalk.bootcamp.dto.GetCertificationInfoDto;
import com.icandoit.boottalk.bootcamp.dto.GetPendingCertificationResponseDto;
import com.icandoit.boottalk.bootcamp.service.BootcampCertificationService;
import com.icandoit.boottalk.social_login.dto.CustomOAuth2User;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users/certification")
@RequiredArgsConstructor
public class BootcampCertificationController {

	private final BootcampCertificationService certificationService;

	// 부트캠프 수료증 등록
	@PostMapping
	public ResponseEntity<CertificationResponseDto> createCertification(
		@AuthenticationPrincipal CustomOAuth2User user,
		@RequestBody CertificationCreationRequestDto request
	) {
		Long userId = user.getServiceUserId();

		CertificationResponseDto response = certificationService.createCertification(
			userId, request
		);

		return ResponseEntity.ok(response);
	}

	// TODO :관리자 권한 추가
	@GetMapping
	public ResponseEntity<List<GetPendingCertificationResponseDto>> getAllCertifications() {
		return ResponseEntity.ok(certificationService.getPendingCertifications());
	}

	// 관리자 권한 추가
	@GetMapping("/{certificationId}")
	public ResponseEntity<GetCertificationInfoDto> getCertification(@PathVariable Long certificationId) {
		return ResponseEntity.ok(certificationService.findById(certificationId));
	}

	@PutMapping
	public ResponseEntity<CertificationResponseDto> updateCertification(@RequestBody CertificationUpdateRequestDto request){
		return ResponseEntity.ok(certificationService.updateCertification(request));
	}
}
