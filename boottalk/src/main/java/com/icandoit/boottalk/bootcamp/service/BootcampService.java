package com.icandoit.boottalk.bootcamp.service;

import static com.icandoit.boottalk.bootcamp.exception.BootcampErrorCode.*;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.icandoit.boottalk.bootcamp.dto.BootcampResponseDto;
import com.icandoit.boottalk.bootcamp.entity.Bootcamp;
import com.icandoit.boottalk.bootcamp.entity.enums.BootcampCategoryType;
import com.icandoit.boottalk.bootcamp.exception.BootcampCustomException;
import com.icandoit.boottalk.bootcamp.repository.BootcampQueryRepository;
import com.icandoit.boottalk.bootcamp.repository.BootcampRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BootcampService {

	private final BootcampRepository bootcampRepository;
	private final BootcampQueryRepository bootcampQueryRepository;

	@Transactional
	public Bootcamp save(Bootcamp bootcamp) {
		return bootcampRepository.save(bootcamp);
	}

	// 부트캠프 단일 조회
	@Transactional(readOnly = true)
	public BootcampResponseDto findById(Long id) {
		Optional<Bootcamp> bootcamp = bootcampRepository.findById(id);

		if (bootcamp.isEmpty()) {
			throw new BootcampCustomException(BOOTCAMP_NOT_FOUND);
		}

		return BootcampResponseDto.from(bootcamp.get());
	}

	// 부트캠프 필터링, 검색 조회
	public Page<BootcampResponseDto> searchBootcamps(
		String region,
		BootcampCategoryType category,
		Integer minRating,
		Integer duration,
		String keyword,
		String sort,
		Pageable pageable
	) {
		Page<Bootcamp> bootcampPage = bootcampQueryRepository.searchBootcampEntities(
			region, category, minRating, duration, keyword, sort, pageable
		);

		List<BootcampResponseDto> dtoList = bootcampPage.getContent().stream()
			.map(BootcampResponseDto::from)
			.toList();

		return new PageImpl<>(dtoList, pageable, bootcampPage.getTotalElements());
	}
}
