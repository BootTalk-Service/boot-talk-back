package com.icandoit.boottalk.bootcamp.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.icandoit.boottalk.bootcamp.dto.BootcampResponse.BootcampResponseDto;
import com.icandoit.boottalk.bootcamp.entity.Bootcamp;
import com.icandoit.boottalk.bootcamp.service.BootcampService;
import com.icandoit.boottalk.libs.dto.SuccessResponseDto;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/bootcamps")
@RequiredArgsConstructor
public class BootcampController {

	private final BootcampService bootcampService;

	// 부트캠프 목록 조회
	@GetMapping
	public ResponseEntity<SuccessResponseDto<List<BootcampResponseDto>>> getAllBootcamps(
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size
	) {
		Pageable pageable = PageRequest.of(page, size);
		Page<Bootcamp> bootcampPage = bootcampService.findAll(pageable);
		List<BootcampResponseDto> bootcampResponseDtoList = BootcampResponseDto.from(bootcampPage.getContent());

		return ResponseEntity.ok(
			SuccessResponseDto.of("부트캠프 목록 조회 성공", bootcampResponseDtoList)
		);
	}

	// 단일 부트캠프 조회
	@GetMapping("/{bootcampId}")
	public ResponseEntity<SuccessResponseDto<BootcampResponseDto>> getBootcamp(@PathVariable Long bootcampId) {
		Bootcamp bootcamp = bootcampService.findById(bootcampId)
			.orElseThrow(() -> new IllegalArgumentException("해당 부트캠프가 존재하지 않습니다."));

		BootcampResponseDto responseDto = BootcampResponseDto.from(bootcamp);

		return ResponseEntity.ok(
			SuccessResponseDto.of("부트캠프 조회 성공", responseDto)
		);
	}
}
