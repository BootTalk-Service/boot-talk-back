package com.icandoit.boottalk.bootcamp.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.icandoit.boottalk.bootcamp.entity.Bootcamp;
import com.icandoit.boottalk.bootcamp.entity.BootcampCategoryType;
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
	public Optional<Bootcamp> findById(Long id) {
		return bootcampRepository.findById(id);
	}

	// 부트캠프 목록 페이징
	public Page<Bootcamp> findAll(Pageable pageable) {
		return bootcampRepository.findAll(pageable);
	}

	// 부트캠프 카테고리 영어로 된 값 한국어로 변환
	public String getKoreanCategoryName(Long id) {
		Optional<Bootcamp> bootcamp = bootcampRepository.findById(id);

		if (bootcamp.isPresent()) {
			BootcampCategoryType category = bootcamp.get().getBootcampCategoryType();
			return category.getKoreanNameByEnglishName(category.name());
		}
		return "해당 부트캠프가 존재하지 않음.";
	}
}
