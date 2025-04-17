package com.icandoit.boottalk.bootcamp.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.icandoit.boottalk.bootcamp.dto.BootcampDetailResponseDto;
import com.icandoit.boottalk.bootcamp.dto.BootcampResponseDto;
import com.icandoit.boottalk.bootcamp.entity.Bootcamp;
import com.icandoit.boottalk.bootcamp.entity.enums.BootcampCategoryType;
import com.icandoit.boottalk.bootcamp.repository.BootcampQueryRepository;
import com.icandoit.boottalk.bootcamp.repository.BootcampRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BootcampService {

	private final BootcampRepository bootcampRepository;
	private final BootcampQueryRepository bootcampQueryRepository;

	// 부트캠프 저장
	@Transactional
	public Bootcamp save(Bootcamp bootcamp) {
		return bootcampRepository.save(bootcamp);
	}

	// 부트캠프 단일 조회
	public BootcampDetailResponseDto findById(Long id) {
		Bootcamp bootcamp = bootcampRepository.getReferenceById(id);

		return BootcampDetailResponseDto.from(bootcamp);
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
