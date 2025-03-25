package com.icandoit.boottalk.bootcamp.service;

import com.icandoit.boottalk.bootcamp.dto.BootcampCreateRequest;
import com.icandoit.boottalk.bootcamp.dto.BootcampUpdateRequest;
import com.icandoit.boottalk.bootcamp.entity.Bootcamp;
import com.icandoit.boottalk.bootcamp.entity.CategoryType;
import com.icandoit.boottalk.bootcamp.entity.TrainingCenter;
import com.icandoit.boottalk.bootcamp.repository.BootcampRepository;
import com.icandoit.boottalk.bootcamp.repository.TrainingCenterRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BootcampService {

	private final BootcampRepository bootcampRepository;
	private final TrainingCenterRepository trainingCenterRepository;

	/**
	 * Bootcamp 생성
	 *
	 * @param request
	 * @return Bootcamp
	 */
	public Bootcamp saveBootcamp(BootcampCreateRequest request) {
		TrainingCenter trainingCenter = findOrCreateTrainingCenter(request.getTcName());
		Bootcamp bootcamp = createBootcamp(request, trainingCenter);
		return bootcampRepository.save(bootcamp);
	}

	/**
	 * TrainingCenter 존재 여부 확인 및 생성
	 *
	 * @param tcName
	 * @return TrainingCenter
	 */
	private TrainingCenter findOrCreateTrainingCenter(String tcName) {
		Optional<TrainingCenter> trainingCenter = trainingCenterRepository.findByName(tcName);

		if (trainingCenter.isPresent()) {
			return trainingCenter.get();
		} else {
			return trainingCenterRepository.save(
				TrainingCenter.builder()
					.name(tcName)
					.build()
			);
		}
	}

	/**
	 * Bootcamp 객체 생성
	 *
	 * @param request
	 * @param trainingCenter
	 * @return Bootcamp
	 */
	private Bootcamp createBootcamp(BootcampCreateRequest request, TrainingCenter trainingCenter) {
		try {
			CategoryType categoryType = CategoryType.fromKoreanName(request.getBtCategory());

			return Bootcamp.builder()
				.trainingCenter(trainingCenter)
				.name(request.getBtName())
				.category(categoryType)
				.degree(request.getBtDegree())
				.region(request.getBtRegion())
				.capacity(request.getBtCapacity())
				.cost(request.getBtCost())
				.startDate(request.getBtStartDate())
				.endDate(request.getBtEndDate())
				.link(request.getBtLink())
				.build();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}


	/**
	 * 부트캠프 단일 조회
	 *
	 * @param id
	 * @return bootcamp
	 */
	public Optional<Bootcamp> findById(Long id) {
		return bootcampRepository.findById(id);
	}

	/**
	 * 부트캠프 목록 페이징 조회
	 *
	 * @param pageable
	 * @return Page<Bootcamp>
	 */
	public Page<Bootcamp> findAll(Pageable pageable) {
		return bootcampRepository.findAll(pageable);
	}

	/**
	 * @param tBtId
	 * @param request
	 * @return Bootcamp
	 */
	public Bootcamp updateBootcamp(Long tBtId, BootcampUpdateRequest request) {
		Bootcamp bootcamp = bootcampRepository.findById(tBtId)
			.orElseThrow(() -> new IllegalArgumentException("해당 부트캠프가 존재하지 않습니다."));

		CategoryType categoryType = CategoryType.fromKoreanName(request.getBtCategory());

		bootcamp.setName(request.getBtName());
		bootcamp.setCategory(categoryType);
		bootcamp.setDegree(request.getBtDegree());
		bootcamp.setRegion(request.getBtRegion());
		bootcamp.setCapacity(request.getBtCapacity());
		bootcamp.setCost(request.getBtCost());
		bootcamp.setStartDate(request.getBtStartDate());
		bootcamp.setEndDate(request.getBtEndDate());
		bootcamp.setLink(request.getBtLink());

		return bootcampRepository.save(bootcamp);
	}

	/**
	 * 부트캠프 삭제
	 *
	 * @param id
	 */
	public void deleteBootcamp(Long id) {
		bootcampRepository.deleteById(id);
	}

	/**
	 * 부트캠프 카테고리 영어로 된 값 한국어로 변환
	 *
	 * @param id
	 * @return (한국어) 부트캠프 카테고리
	 */
	public String getKoreanCategoryName(Long id) {
		Optional<Bootcamp> bootcamp = bootcampRepository.findById(id);

		if (bootcamp.isPresent()) {
			CategoryType category = bootcamp.get().getCategory();
			return category.getKoreanNameByEnglishName(category.name());
		}
		return "해당 부트캠프가 존재하지 않음.";
	}
}
