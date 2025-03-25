package com.icandoit.boottalk.bootcamp.controller;

import com.icandoit.boottalk.bootcamp.dto.BootcampCreateRequest;
import com.icandoit.boottalk.bootcamp.dto.BootcampResponse;
import com.icandoit.boottalk.bootcamp.dto.BootcampResponse.BootcampResponseDto;
import com.icandoit.boottalk.bootcamp.dto.BootcampResponse.Response;
import com.icandoit.boottalk.bootcamp.dto.BootcampUpdateRequest;
import com.icandoit.boottalk.bootcamp.entity.Bootcamp;
import com.icandoit.boottalk.bootcamp.service.BootcampService;
import com.icandoit.boottalk.dto.BaseResponse;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bootcamps")
@RequiredArgsConstructor
public class BootcampController {

	private final BootcampService bootcampService;

	/**
	 * 부트캠프 생성
	 *
	 * @param request
	 */
	@PostMapping
	public ResponseEntity<?> createBootcamp(
		@RequestBody BootcampCreateRequest request
	) {
		try {
			Bootcamp createdBootcamp = bootcampService.saveBootcamp(request);
			BootcampResponse.BootcampResponseDto responseDto = new BootcampResponse.BootcampResponseDto(
				createdBootcamp);
			BootcampResponse.Response response = new Response("등록이 완료되었습니다.", responseDto);

			return ResponseEntity.ok(response);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
		}
	}

	/**
	 * 모든 캠프 조회
	 *
	 * @param page
	 * @param size
	 */
	@GetMapping
	public ResponseEntity<?> getAllBootcamps(
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size
	) {
		Pageable pageable = PageRequest.of(page, size);
		Page<Bootcamp> bootcampPage = bootcampService.findAll(pageable);

		List<BootcampResponseDto> bootcampResponseDtoList = bootcampPage.getContent().stream()
			.map(BootcampResponse.BootcampResponseDto::new)
			.toList();
		return ResponseEntity.ok(new Response("부트캠프 목록 조회 성공", bootcampResponseDtoList));
	}

	/**
	 * 부트캠프 상세 정보 조회
	 *
	 * @param tBtId
	 */
	@GetMapping("/{tBtId}")
	public ResponseEntity<?> getBootcamp(@PathVariable Long tBtId) {
		Optional<Bootcamp> bootcamp = bootcampService.findById(tBtId);

		if (bootcamp.isPresent()) {
			BootcampResponse.BootcampResponseDto responseDto = new BootcampResponse.BootcampResponseDto(
				bootcamp.get());
			BootcampResponse.Response response = new Response("부트캠프 조회 성공", responseDto);
			return ResponseEntity.ok(response);
		}
		return ResponseEntity.status(404)
			.body(createErrorResponse("해당 부트캠프가 존재하지 않습니다."));
	}

	/**
	 * 부트캠프 수정
	 *
	 * @param tBtId
	 * @param request
	 */
	@PutMapping("/{tBtId}")
	public ResponseEntity<?> updateBootcamp(
		@PathVariable Long tBtId,
		@RequestBody BootcampUpdateRequest request
	) {
		try {
			Bootcamp updatedBootcamp = bootcampService.updateBootcamp(tBtId, request);
			BootcampResponse.BootcampResponseDto responseDto = new BootcampResponse.BootcampResponseDto(
				updatedBootcamp);
			BootcampResponse.Response response = new Response("수정이 완료되었습니다.", responseDto);
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			return createErrorResponse(e.getMessage());
		}
	}

	/**
	 * 부트캠프 삭제
	 *
	 * @param tBtId
	 */
	@DeleteMapping("/{tBtId}")
	public ResponseEntity<?> deleteBootcamp(@PathVariable Long tBtId) {
		try {
			bootcampService.deleteBootcamp(tBtId);
			return ResponseEntity.ok(new BaseResponse("부트캠프 삭제가 완료되었습니다."));
		} catch (Exception e) {
			return createErrorResponse(e.getMessage());
		}
	}

	/**
	 * 에러 리스폰스 생성 메소드
	 * @param message
	 */
	private ResponseEntity<?> createErrorResponse(String message) {
		return ResponseEntity.badRequest().body(new BaseResponse(message));
	}
}
