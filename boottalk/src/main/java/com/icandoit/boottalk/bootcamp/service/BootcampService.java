package com.icandoit.boottalk.bootcamp.service;

import static com.icandoit.boottalk.bootcamp.exception.BootcampErrorCode.*;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.icandoit.boottalk.bootcamp.dto.BootcampResponseDto;
import com.icandoit.boottalk.bootcamp.entity.Bootcamp;
import com.icandoit.boottalk.bootcamp.exception.BootcampCustomException;
import com.icandoit.boottalk.bootcamp.repository.BootcampRepository;

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
	@Transactional(readOnly = true)
	public BootcampResponseDto findById(Long id) {
		Optional<Bootcamp> bootcamp = bootcampRepository.findById(id);

		if (bootcamp.isEmpty()) {
			throw new BootcampCustomException(BOOTCAMP_NOT_FOUND);
		}

		return BootcampResponseDto.from(bootcamp.get());
	}

	// 부트캠프 목록 페이징
	// TODO : 평균 평점도 같이 리턴해야 함 - 기본조건 진행중인 부트캠프여야 할 예정
	// TODO : 필터 : 지역(시로 구분), 기간별(최신순, 오래된 순) - 개강 날짜, 직무별, 평점(점수대 별로 0 ~ 1, 1 ~ 2, 2 ~ 3, 3 이상)
	@Transactional(readOnly = true)
	public Page<Bootcamp> findAll(Pageable pageable) {
		return bootcampRepository.findAll(pageable);
	}

	// TODO : 해당 부트캠프의 리뷰 전체 조회


}
