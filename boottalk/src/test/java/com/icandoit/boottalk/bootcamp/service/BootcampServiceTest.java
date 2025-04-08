package com.icandoit.boottalk.bootcamp.service;

import static com.icandoit.boottalk.libs.exception.ErrorCode.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.icandoit.boottalk.bootcamp.dto.BootcampResponseDto;
import com.icandoit.boottalk.bootcamp.entity.Bootcamp;
import com.icandoit.boottalk.bootcamp.entity.Course;
import com.icandoit.boottalk.bootcamp.entity.TrainingCenter;
import com.icandoit.boottalk.bootcamp.entity.enums.BootcampCategoryType;
import com.icandoit.boottalk.bootcamp.repository.BootcampQueryRepository;
import com.icandoit.boottalk.bootcamp.repository.BootcampRepository;
import com.icandoit.boottalk.libs.exception.CustomException;

class BootcampServiceTest {

	@InjectMocks
	private BootcampService bootcampService;

	@Mock
	private BootcampRepository bootcampRepository;

	@Mock
	private BootcampQueryRepository bootcampQueryRepository;

	private Bootcamp bootcamp;
	private TrainingCenter trainingCenter;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);

		trainingCenter = TrainingCenter.builder()
			.trainingCenterName("기관명")
			.trainingCenterAddress("기관 주소")
			.trainingCenterEmail("example@test.com")
			.trainingCenterPhoneNumber("010-1234-5678")
			.trainingCenterUrl("www.trainingcenter.com")
			.build();

		Course course = Course.of("TPID-001", "테스트 코스", BootcampCategoryType.APPLICATION_SW_ENGINEERING, trainingCenter);
		course.updateReviewStats(8, 2); // 예시: 평균 평점 4.0

		bootcamp = Bootcamp.builder()
			.bootcampId(1L)
			.trainingCenter(trainingCenter)
			.course(course) // ★ 여기가 핵심
			.bootcampName("테스트 캠프")
			.bootcampDegree(1)
			.bootcampRegion("서울")
			.bootcampCapacity(10)
			.bootcampCost(true)
			.bootcampStartDate(LocalDate.of(2025, 4, 1))
			.bootcampEndDate(LocalDate.of(2025, 6, 30))
			.bootcampLink("www.testcamp.com")
			.bootcampCategoryType(BootcampCategoryType.APPLICATION_SW_ENGINEERING)
			.build();
	}


	@Test
	@DisplayName("부트캠프 저장 성공")
	void saveTestSuccess() {
		//given
		when(bootcampRepository.save(bootcamp)).thenReturn(bootcamp);

		//when
		Bootcamp savedBootcamp = bootcampService.save(bootcamp);

		//then
		assertEquals(savedBootcamp.getBootcampId(), bootcamp.getBootcampId());
		assertEquals(savedBootcamp.getBootcampName(), bootcamp.getBootcampName());
		assertEquals(savedBootcamp.getBootcampCategoryType(), bootcamp.getBootcampCategoryType());
		assertEquals(savedBootcamp.getBootcampDegree(), bootcamp.getBootcampDegree());
		assertEquals(savedBootcamp.getBootcampRegion(), bootcamp.getBootcampRegion());
		assertEquals(savedBootcamp.getBootcampCapacity(), bootcamp.getBootcampCapacity());
		assertEquals(savedBootcamp.isBootcampCost(), bootcamp.isBootcampCost());
		assertEquals(savedBootcamp.getBootcampStartDate(), bootcamp.getBootcampStartDate());
		assertEquals(savedBootcamp.getBootcampEndDate(), bootcamp.getBootcampEndDate());
		assertEquals(savedBootcamp.getBootcampLink(), bootcamp.getBootcampLink());
		assertEquals(savedBootcamp.getTrainingCenter().getTrainingCenterId(), trainingCenter.getTrainingCenterId());
		verify(bootcampRepository, times(1)).save(bootcamp);
	}

	@Test
	@DisplayName("부트캠프 ID로 조회 성공")
	void findByIdSuccess() {
		//given
		when(bootcampRepository.findById(1L)).thenReturn(Optional.of(bootcamp));

		//when
		BootcampResponseDto response = bootcampService.findById(1L);
		BootcampCategoryType categoryType = BootcampCategoryType.fromKoreanName(response.bootcampCategory());

		//then
		assertEquals(response.bootcampId(), bootcamp.getBootcampId());
		assertEquals(response.bootcampName(), bootcamp.getBootcampName());
		assertEquals(categoryType, bootcamp.getBootcampCategoryType());
		assertEquals(response.bootcampDegree(), bootcamp.getBootcampDegree());
		assertEquals(response.bootcampRegion(), bootcamp.getBootcampRegion());
		assertEquals(response.bootcampCapacity(), bootcamp.getBootcampCapacity());
		assertEquals(response.bootcampCost(), bootcamp.isBootcampCost());
		assertEquals(response.bootcampStartDate(), bootcamp.getBootcampStartDate());
		assertEquals(response.bootcampEndDate(), bootcamp.getBootcampEndDate());
		assertEquals(response.bootcampLink(), bootcamp.getBootcampLink());
		verify(bootcampRepository, times(1)).findById(1L);
	}

	@Test
	@DisplayName("부트캠프 ID로 조회 실패 - 부트캠프 없음")
	void findByIdFailedBootcampNotFound() {
		//given
		when(bootcampRepository.findById(999L)).thenReturn(Optional.empty());

		//when & then
		CustomException exception = assertThrows(CustomException.class,
			() -> bootcampService.findById(999L)
		);

		assertEquals(exception.getErrorCode(), BOOTCAMP_NOT_FOUND);
		verify(bootcampRepository, times(1)).findById(999L);
	}

	@Test
	@DisplayName("부트캠프 검색 성공 - 필터 및 키워드 포함")
	void searchBootcampsWithFiltersAndKeyword() {
		// given
		String region = "서울";
		BootcampCategoryType category = BootcampCategoryType.APPLICATION_SW_ENGINEERING;
		Integer minRating = 3;
		Integer duration = 2;
		String keyword = "테스트";
		String sort = "latest";
		Pageable pageable = PageRequest.of(0, 10);

		Page<Bootcamp> mockPage = new PageImpl<>(List.of(bootcamp), pageable, 1);

		when(bootcampQueryRepository.searchBootcampEntities(
			region, category, minRating, duration, keyword, sort, pageable
		)).thenReturn(mockPage);

		// when
		Page<BootcampResponseDto> result = bootcampService.searchBootcamps(
			region, category, minRating, duration, keyword, sort, pageable
		);

		// then
		assertEquals(1, result.getTotalElements());
		assertEquals(bootcamp.getBootcampName(), result.getContent().get(0).bootcampName());
		verify(bootcampQueryRepository, times(1)).searchBootcampEntities(
			region, category, minRating, duration, keyword, sort, pageable
		);
	}

	@Test
	@DisplayName("부트캠프 검색 테스트 - 빈 결과 반환")
	void searchBootcampsReturnsEmpty() {
		// given
		Pageable pageable = PageRequest.of(0, 10);
		when(bootcampQueryRepository.searchBootcampEntities(
			null, null, null, null, null, null, pageable
		)).thenReturn(Page.empty(pageable));

		// when
		Page<BootcampResponseDto> result = bootcampService.searchBootcamps(
			null, null, null, null, null, null, pageable
		);

		// then
		assertEquals(0, result.getTotalElements());
		verify(bootcampQueryRepository, times(1)).searchBootcampEntities(
			null, null, null, null, null, null, pageable
		);
	}
}