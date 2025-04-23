package com.icandoit.boottalk.bootcamp.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.icandoit.boottalk.bootcamp.dto.CertificationResponseDto;
import com.icandoit.boottalk.bootcamp.dto.CertificationUpdateRequestDto;
import com.icandoit.boottalk.bootcamp.dto.GetCertificationInfoDto;
import com.icandoit.boottalk.bootcamp.dto.GetPendingCertificationResponseDto;
import com.icandoit.boottalk.bootcamp.service.BootcampCertificationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/certification")
@RequiredArgsConstructor
public class BootcampCertificationController {

	private final BootcampCertificationService certificationService;

	@GetMapping
	public ResponseEntity<List<GetPendingCertificationResponseDto>> getAllCertifications() {
		return ResponseEntity.ok(certificationService.getPendingCertifications());
	}

	@GetMapping("/{certificationId}")
	public ResponseEntity<GetCertificationInfoDto> getCertification(
		@PathVariable Long certificationId) {
		return ResponseEntity.ok(certificationService.findById(certificationId));
	}

	@PutMapping
	public ResponseEntity<CertificationResponseDto> updateCertification(@RequestBody CertificationUpdateRequestDto request){
		return ResponseEntity.ok(certificationService.updateCertification(request));
	}
}
