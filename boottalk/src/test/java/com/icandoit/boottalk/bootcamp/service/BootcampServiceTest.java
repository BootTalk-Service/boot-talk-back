package com.icandoit.boottalk.bootcamp.service;

import static com.icandoit.boottalk.bootcamp.exception.BootcampErrorCode.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.icandoit.boottalk.bootcamp.dto.BootcampResponse.BootcampResponseDto;
import com.icandoit.boottalk.bootcamp.entity.Bootcamp;
import com.icandoit.boottalk.bootcamp.entity.BootcampCategoryType;
import com.icandoit.boottalk.bootcamp.entity.TrainingCenter;
import com.icandoit.boottalk.bootcamp.exception.BootcampCustomException;
import com.icandoit.boottalk.bootcamp.repository.BootcampRepository;

class BootcampServiceTest {

	@InjectMocks
	private BootcampService bootcampService;

	@Mock
	private BootcampRepository bootcampRepository;

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

		bootcamp = Bootcamp.builder()
			.bootcampId(1L)
			.trainingCenter(trainingCenter)
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
	@DisplayName("부트캠프 저장 테스트")
	void saveTest() {
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
	@DisplayName("부트캠프 ID 조회 테스트 - 성공")
	void findByIdSuccess() {
		//given
		when(bootcampRepository.findById(1L)).thenReturn(Optional.of(bootcamp));

		//when
		BootcampResponseDto response = bootcampService.findById(1L);
		BootcampCategoryType categoryType = BootcampCategoryType.fromKoreanName(response.getBootcampCategory());

		//then
		assertEquals(response.getBootcampId(), bootcamp.getBootcampId());
		assertEquals(response.getBootcampName(), bootcamp.getBootcampName());
		assertEquals(categoryType, bootcamp.getBootcampCategoryType());
		assertEquals(response.getBootcampDegree(), bootcamp.getBootcampDegree());
		assertEquals(response.getBootcampRegion(), bootcamp.getBootcampRegion());
		assertEquals(response.getBootcampCapacity(), bootcamp.getBootcampCapacity());
		assertEquals(response.isBootcampCost(), bootcamp.isBootcampCost());
		assertEquals(response.getBootcampStartDate(), bootcamp.getBootcampStartDate());
		assertEquals(response.getBootcampEndDate(), bootcamp.getBootcampEndDate());
		assertEquals(response.getBootcampLink(), bootcamp.getBootcampLink());
		verify(bootcampRepository, times(1)).findById(1L);
	}

	@Test
	@DisplayName("부트캠프 ID 조회 실패 테스트 - 부트캠프 없음")
	void findByIdFailedBootcampNotFound() {
		//given
		when(bootcampRepository.findById(999L)).thenReturn(Optional.empty());

		//when & then
		BootcampCustomException exception = assertThrows(BootcampCustomException.class,
			() -> bootcampService.findById(999L)
		);

		assertEquals(exception.getBootcampErrorCode(), BOOTCAMP_NOT_FOUND);
		verify(bootcampRepository, times(1)).findById(999L);
	}
}