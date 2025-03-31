package com.icandoit.boottalk.bootcamp.service;

import static com.icandoit.boottalk.bootcamp.dto.BootcampResponse.*;
import static com.icandoit.boottalk.bootcamp.exception.BootcampErrorCode.*;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.icandoit.boottalk.bootcamp.entity.Bootcamp;
import com.icandoit.boottalk.bootcamp.exception.BootcampCustomException;
import com.icandoit.boottalk.bootcamp.repository.BootcampRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BootcampService {

	private final BootcampRepository bootcampRepository;

	@Transactional
	public Bootcamp save(Bootcamp bootcamp) {
		return bootcampRepository.save(bootcamp);
	}

	// 부트캠프 단일 조회
	public BootcampResponseDto findById(Long id) {
		Optional<Bootcamp> bootcamp = bootcampRepository.findById(id);

		if (bootcamp.isEmpty()) {
			throw new BootcampCustomException(BOOTCAMP_NOT_FOUND);
		}

		return BootcampResponseDto.from(bootcamp.get());
	}

	// 부트캠프 목록 페이징
	public Page<Bootcamp> findAll(Pageable pageable) {
		return bootcampRepository.findAll(pageable);
	}
}
